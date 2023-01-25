package app.sato.kchan.tasks

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.sato.kchan.tasks.databinding.TimeActivityBinding
import app.sato.kchan.tasks.fanction.Note
import app.sato.kchan.tasks.fanction.NoteManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeActivity: AppCompatActivity(){
    private lateinit var binding: TimeActivityBinding

    val nm = NoteManager()
    var start = true // falseならend
    var startDateTimeList = mutableListOf<Int>()
    var endDateList = mutableListOf<Int>()
    var note = ""

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = TimeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        note = intent.getStringExtra("note").toString()
        nm.receive(note)
        val receivedNote = nm.getNote()!!

        val startTime = receivedNote.getNoticeShow()
        if (startTime != null) {
            binding.timeSettingSwitch.isChecked = true
            binding.timeStartText.isVisible = true
            binding.timeStartSettingButton.isVisible = true
            binding.timeEndText.isVisible = true
            binding.timeEndSettingButton.isVisible = true

            startDateTimeList.add(startTime.year)
            startDateTimeList.add(startTime.monthValue)
            startDateTimeList.add(startTime.dayOfMonth)
            startDateTimeList.add(startTime.hour)
            startDateTimeList.add(startTime.minute)

            binding.timeStartText.text = "通知開始時間 : ${DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(receivedNote.getNoticeShow())}"
        }
        if (receivedNote.getNoticeHide() != null) {
            binding.timeEndText.text = "通知終了時間 : ${DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(receivedNote.getNoticeHide())}"
        }

        binding.timeSettingSwitch.isChecked = receivedNote.getNoticeShow() != null

        binding.timeSettingSwitch.setOnCheckedChangeListener { _, isChecked ->
            // トグルがONの時の処理
            if (isChecked) {
                binding.timeStartText.isVisible = true
                binding.timeStartSettingButton.isVisible = true
                binding.timeEndText.isVisible = true
                binding.timeEndSettingButton.isVisible = true
            }
            // トグルがOFFの時の処理
            else {
                binding.timeStartText.isVisible = false
                binding.timeStartSettingButton.isVisible = false
                binding.timeEndText.isVisible = false
                binding.timeEndSettingButton.isVisible = false
            }
        }

        // 通知開始時間設定ボタンタップ処理
        binding.timeStartSettingButton.setOnClickListener {
            start = true
            showDatePickerDialog()
        }

        // 通知終了時間設定ボタンタップ処理
        binding.timeEndSettingButton.setOnClickListener {
            if (receivedNote.getNoticeShow() == null) Toast.makeText(this, "開始時間を設定してください", Toast.LENGTH_LONG).show()
            else {
                start = false
                showDatePickerDialog()
            }
        }

        val toolbar = binding.timeToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home -> {
                    val receivedNote = nm.getNote()!!
                    if (!binding.timeSettingSwitch.isChecked) {
                        receivedNote.setNoticeShow(null)
                        receivedNote.setNoticeHide(null)
                    } else {
                        setAlarm(applicationContext, receivedNote)
                    }
                    finish()
                }
            }
        return super.onOptionsItemSelected(item)
    }

    // 日付選択のダイアログ生成
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            // メモ：monthは0-11なので+1する必要がある
            if (start) {
                startDateTimeList.clear()
                startDateTimeList.add(0, year)
                startDateTimeList.add(1, month+1)
                startDateTimeList.add(2, day)
                showTimePickerDialog()
            }
            else {
                // 終了年月日保存処理
                val startDate = LocalDate.of(startDateTimeList[0], startDateTimeList[1], startDateTimeList[2])
                val settingDate = LocalDate.of(year, month+1, day)
                if (settingDate.isBefore(startDate)) {
                    Toast.makeText(this, "終了日時は開始日時より後に設定してください", Toast.LENGTH_LONG).show()
                } else {
                    endDateList.add(0, year)
                    endDateList.add(1, month+1)
                    endDateList.add(2, day)
                    showTimePickerDialog()
                }
            }
        }

        //日付ピッカーダイアログを生成および設定
        if (start) {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
            }.show()
        } else {
            DatePickerDialog(
                this,
                dateSetListener,
                startDateTimeList[0],
                startDateTimeList[1] - 1,
                startDateTimeList[2]
            ).apply {
            }.show()
        }
    }

    // 時間選択のダイアログ生成
    @SuppressLint("SetTextI18n")
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            val showFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
            val n = nm.getNote()!!

            if (start) {
                startDateTimeList.add(3, hour)
                startDateTimeList.add(4, minute)
                val startDateTime = LocalDateTime.of(startDateTimeList[0], startDateTimeList[1], startDateTimeList[2], startDateTimeList[3], startDateTimeList[4], 0)
                binding.timeStartText.text = "通知開始時間 : ${startDateTime.format(showFormat)}"
                n.setNoticeShow(startDateTime)
            }
            else {
                val startDateTime = LocalDateTime.of(startDateTimeList[0], startDateTimeList[1], startDateTimeList[2], startDateTimeList[3], startDateTimeList[4], 0)
                val settingTime = LocalDateTime.of(endDateList[0], endDateList[1], endDateList[2], hour, minute, 0)
                if (settingTime.isBefore(startDateTime) || settingTime.isEqual(startDateTime)) {
                    Toast.makeText(this, "終了時間は開始時間より後に設定してください", Toast.LENGTH_LONG).show()
                } else {
                    binding.timeEndText.text = "通知終了時間 : ${settingTime.format(showFormat)}"

                    n.setNoticeHide(settingTime)
                }
            }
        }
        //タイムピッカーダイアログを生成および設定
        if (start) {
            TimePickerDialog(
                this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true)
                .show()
        } else {
            if (startDateTimeList[4]+1 == 60) {
                TimePickerDialog(
                    this,
                    timeSetListener,
                    startDateTimeList[3] + 1,
                    0,
                    true
                )
                    .show()
            } else {
                TimePickerDialog(
                    this,
                    timeSetListener,
                    startDateTimeList[3],
                    startDateTimeList[4] + 1,
                    true
                )
                    .show()
            }
        }
    }

    fun setAlarm(context: Context, note: Note) {
        val intent = Intent(context, AlarmNotification::class.java)

        val start = note.getNoticeShow()!!
        val startCalendar = Calendar.getInstance()
        val stop = note.getNoticeHide()
        val stopCalendar = Calendar.getInstance()

        if ((start.isAfter(LocalDateTime.now()) || start.isEqual(LocalDateTime.now())) && stop != null) {
            startCalendar.set(start.year, start.monthValue-1, start.dayOfMonth, start.hour, start.minute, 0)
            stopCalendar.set(stop.year, stop.monthValue-1, stop.dayOfMonth, stop.hour, stop.minute, 0)
            intent.putExtra("deleteTime", stopCalendar.timeInMillis - startCalendar.timeInMillis)
        } else if (start.isAfter(LocalDateTime.now()) || start.isEqual(LocalDateTime.now())) {
            startCalendar.set(start.year, start.monthValue-1, start.dayOfMonth, start.hour, start.minute, 0)
        } else if (stop == null) {
            return
        } else if (start.isBefore(LocalDateTime.now()) && (stop.isAfter(LocalDateTime.now()) || stop.isEqual(LocalDateTime.now()))) {
            val now = LocalDateTime.now()
            startCalendar.set(now.year, now.monthValue-1, now.dayOfMonth, now.hour, now.minute, now.second)
            stopCalendar.set(stop.year, stop.monthValue-1, stop.dayOfMonth, stop.hour, stop.minute, 0)
            intent.putExtra("deleteTime", stopCalendar.timeInMillis - startCalendar.timeInMillis)
        } else {
            return
        }

        val uuid = UUID.randomUUID().hashCode()
        intent.putExtra("id", uuid)
        intent.putExtra("title", note.getTitle())
        intent.putExtra("content", note.getContent())

        val pending = PendingIntent.getBroadcast(
            context, uuid, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // アラームをセットする
        val am = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        am.setExact(
            AlarmManager.RTC_WAKEUP,
            startCalendar.getTimeInMillis(), pending
        )
    }

//    fun cancelAlarm(context: Context) {
//        val intent = Intent(context, AlarmNotification::class.java)
//        val pending = PendingIntent.getBroadcast(
//            context, uuid, intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // アラームを解除する
//        val am = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
//        am.cancel(pending)
//        }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}