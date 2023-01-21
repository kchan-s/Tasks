package app.sato.kchan.tasks

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.sato.kchan.tasks.databinding.TimeActivityBinding
import app.sato.kchan.tasks.fanction.NoteManager
import java.util.*

class TimeActivity: AppCompatActivity(){
    private lateinit var binding: TimeActivityBinding
    var start = true // falseならend
    var startText = ""
    var endText = ""
    var startDateTimeList = mutableListOf<Int>()
    var position = -1

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = TimeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }
        position = intent.getIntExtra("position", -1)

        val nm = NoteManager()
        nm.selectByTempId(position.toString())
        val n = nm.getNote()

        if (n.getNoticeShow() != null) {
            binding.timeSettingSwitch.isChecked = true
            binding.timeStartText.isVisible = true
            binding.timeStartSettingButton.isVisible = true
            binding.timeEndText.isVisible = true
            binding.timeEndSettingButton.isVisible = true
            binding.timeStartText.text = "通知開始時間 : ${n.getNoticeShow()}"
        }
        if (n.getNoticeHide() != null) binding.timeEndText.text = "通知終了時間 : ${n.getNoticeHide()}"

        // トグルの値読み込みが必要
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
                //del (time and date)settingが必要
            }
        }

        // 通知開始時間設定ボタンタップ処理
        binding.timeStartSettingButton.setOnClickListener {
            start = true
            showDatePickerDialog()
        }

        // 通知終了時間設定ボタンタップ処理
        binding.timeEndSettingButton.setOnClickListener {
            if (n.getNoticeShow() == null) Toast.makeText(this, "開始時間を設定してください", Toast.LENGTH_LONG).show()
            else {
                start = false
                showDatePickerDialog()
            }
            //終了時間に設定？
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

            // メモ：monthは0-11らしいので+1する必要がある
            if (start) {
                // 開始年月日保存処理
                startDateTimeList.add(0, year)
                startDateTimeList.add(1, month)
                startDateTimeList.add(2, day)
                startText = "${year}/${month+1}/${day}"
            }
            else {
                // 終了年月日保存処理
                if (startDateTimeList[0] <= year && startDateTimeList[1] <= month && startDateTimeList[2] <= day) {
                    endText = "${year}/${month+1}/${day}"
                } else {
                    Toast.makeText(this, "終了時間は開始時間より後に設定してください", Toast.LENGTH_LONG).show()
                }
            }
            showTimePickerDialog()
        }

        //日付ピッカーダイアログを生成および設定
        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
        }.show()
    }

    // 時間選択のダイアログ生成
    @SuppressLint("SetTextI18n")
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            if (start) {
                // 開始時刻保存処理
                startDateTimeList.add(3, hour)
                startDateTimeList.add(4, minute)
                if (hour < 10 && minute < 10) startText = "$startText 0$hour:0$minute"
                else if (hour < 10) startText = "$startText 0$hour:$minute"
                else if (minute < 10) startText = "$startText $hour:0$minute"
                else startText = "$startText $hour:$minute"
                binding.timeStartText.text = "通知開始時間 : ${startText}"
            }
            else {
                if (startDateTimeList[3] <= hour && startDateTimeList[4] <= minute) {
                    // 終了時刻保存処理
                    if (hour < 10 && minute < 10) endText = "$endText 0$hour:0$minute"
                    else if (hour < 10) endText = "$endText 0$hour:$minute"
                    else if (minute < 10) endText = "$endText $hour:0$minute"
                    else endText = "$endText $hour:$minute"
                    binding.timeEndText.text = "通知終了時間 : ${endText}"
                } else {
                    Toast.makeText(this, "終了時間は開始時間より後に設定してください", Toast.LENGTH_LONG).show()
                }
            }
        }
        //タイムピッカーダイアログを生成および設定
        TimePickerDialog(
            this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true)
            .show()
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}