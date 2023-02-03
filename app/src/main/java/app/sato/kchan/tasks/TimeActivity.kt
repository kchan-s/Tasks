package app.sato.kchan.tasks

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import app.sato.kchan.tasks.databinding.TimeActivityBinding
import app.sato.kchan.tasks.fanction.Note
import app.sato.kchan.tasks.fanction.NoteManager
import app.sato.kchan.tasks.fanction.NoticeManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeActivity: AppCompatActivity(), LocationListener{
    private lateinit var binding: TimeActivityBinding

    val noteManager = NoteManager()
    var startDateTime: LocalDateTime? = null
    var endDateTime: LocalDateTime? = null
    var start = true // falseならend
    var startDateTimeList = mutableListOf<Int>()
    var endDateList = mutableListOf<Int>()
    var received = ""

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = TimeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        received = intent.getStringExtra("received").toString()
        noteManager.receive(received)
        val receivedNote = noteManager.getNote()!!

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
                startDateTime = null
                endDateTime = null
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
                    val note = noteManager.getNote()!!
                    val uuid: Int
                    if (note.getNoticeBarId() == 0) {
                        uuid = UUID.randomUUID().hashCode()
                        note.setNoticeBarId(uuid)
                    } else {
                        uuid = note.getNoticeBarId()!!
                    }

                    val notificationManager =
                        HomeActivity.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.getNotificationChannel("notification")
                    notificationManager.cancel(uuid)
                    ForegroundNotificationService().cancelAlarm(applicationContext, uuid)

                    if (startDateTime == null && endDateTime == null) {
                        note.setNoticeShow(null)
                        note.setNoticeHide(null)
                    }

                    if (note.getNoticeLocation() != null) {
                        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1F, this)
                        }
                    } else if (startDateTime != null){
                        ForegroundNotificationService().setAlarm(applicationContext, note, uuid)
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
            // メモ：monthは0-11なので+1する必要がある
            if (start) {
                startDateTimeList.clear()
                startDateTimeList.add(0, year)
                startDateTimeList.add(1, month+1)
                startDateTimeList.add(2, day)
                showTimePickerDialog()
            } else {
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
            val showFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
            val note = noteManager.getNote()!!

            if (start) {
                startDateTimeList.add(3, hour)
                startDateTimeList.add(4, minute)
                startDateTime = LocalDateTime.of(startDateTimeList[0], startDateTimeList[1], startDateTimeList[2], startDateTimeList[3], startDateTimeList[4], 0)
                binding.timeStartText.text = "通知開始時間 : ${startDateTime!!.format(showFormat)}"
                note.setNoticeShow(startDateTime)
            }
            else {
                startDateTime = LocalDateTime.of(startDateTimeList[0], startDateTimeList[1], startDateTimeList[2], startDateTimeList[3], startDateTimeList[4], 0)
                endDateTime = LocalDateTime.of(endDateList[0], endDateList[1], endDateList[2], hour, minute, 0)
                if (endDateTime!!.isBefore(startDateTime) || endDateTime!!.isEqual(startDateTime)) {
                    Toast.makeText(this, "終了時間は開始時間より後に設定してください", Toast.LENGTH_LONG).show()
                } else {
                    binding.timeEndText.text = "通知終了時間 : ${endDateTime!!.format(showFormat)}"
                    note.setNoticeHide(endDateTime)
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

    override fun onLocationChanged(location: android.location.Location) {
        val notificationManager =
            HomeActivity.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.getNotificationChannel("notification")
        notificationManager.cancelAll()

        val noticeManager = NoticeManager()
        val locationManager = app.sato.kchan.tasks.fanction.LocationManager()
        locationManager.searchByRadius(
            location.latitude.toFloat(),
            location.longitude.toFloat(),
            75
        )
        if (locationManager.isLocation()) {
            do {
                noticeManager.searchByLocation(locationManager.getLocation()!!)
                if (noticeManager.getNoticeNumber() != 0) {
                    val note = noticeManager.getNote()!!
                    val uuid: Int
                    if (note.getNoticeBarId() == 0) {
                        uuid = UUID.randomUUID().hashCode()
                        note.setNoticeBarId(uuid)
                    } else {
                        uuid = note.getNoticeBarId()!!
                    }
                    if (!note.isComplete() && note.getNoticeShow() == null) {
                        val mainIntent = Intent(HomeActivity.context, HomeActivity::class.java).apply() {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }

                        val pendingIntent: PendingIntent =
                            PendingIntent.getActivity(HomeActivity.context, 0, mainIntent, 0)
                        val channelId = "notification"
                        val channel = NotificationChannel(
                            channelId, "通知",
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            setSound(null, null)
                        }

//                         Register the channel with the system
                        val notificationManager =
                            HomeActivity.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(channel)

                        //通知オブジェクトの作成
                        val builder = NotificationCompat.Builder(HomeActivity.context, channelId)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(note.getTitle())
                            .setContentText(note.getContent())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)

                        val notification = builder.build()
                        notification.flags = Notification.FLAG_NO_CLEAR

                        //通知の実施
                        notificationManager.notify(uuid, notification)
                    } else if (!note.isComplete()) {
                        ForegroundNotificationService().setAlarm(HomeActivity.context, note, uuid)
                    }
                }
            } while (locationManager.next())
        }
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_DayNight))
    }
}