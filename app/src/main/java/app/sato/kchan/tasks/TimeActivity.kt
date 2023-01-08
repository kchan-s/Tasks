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
import java.util.*

class TimeActivity: AppCompatActivity(){
    private lateinit var binding: TimeActivityBinding
    var start = true // falseならend
    var startTimeSetting = false // 開始時間が設定されているか
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

        // トグルの値読み込みが必要
        binding.timeSetting.setOnCheckedChangeListener { _, isChecked ->
            // トグルがONの時の処理
            if (isChecked) {
                binding.start.isVisible = true
                binding.startText.isVisible = true
                binding.end.isVisible = true
                binding.endText.isVisible = true

                // ↓showButton_onClick()
                binding.start.setOnClickListener {
                    start = true
                    startTimeSetting = true
                    showDatePickerDialog()
                    //開始時刻に設定？
                }

                // ↓hideButton_onClick()
                binding.end.setOnClickListener {
                    if (!startTimeSetting) Toast.makeText(this, "開始時間を設定してください", Toast.LENGTH_LONG).show()
                    else {
                        start = false
                        showDatePickerDialog()
                    }
                    //終了時間に設定？
                }
            }
            // トグルがOFFの時の処理
            else {
                binding.start.isVisible = false
                binding.startText.isVisible = false
                binding.end.isVisible = false
                binding.endText.isVisible = false
                //del (time and date)settingが必要
            }
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                if (startText != "" && endText != "") {
                    HomeMemoListAdapter.settingData[position] = "$startText 〜 $endText"
                } else if (startText != "") {
                    HomeMemoListAdapter.settingData[position] = "$startText 〜"
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 日付選択のダイアログ生成
    fun showDatePickerDialog() {
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
    fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            if (start) {
                // 開始時刻保存処理
                startDateTimeList.add(3, hour)
                startDateTimeList.add(4, minute)
                startText = "$startText $hour:$minute"
                binding.startText.text = "通知開始時間 : ${startText}"
            }
            else {
                if (startDateTimeList[3] <= hour && startDateTimeList[4] <= minute) {
                    // 終了時刻保存処理
                    endText = "$endText $hour:${minute+1}"
                    binding.endText.text = "通知終了時間 : ${endText}"
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