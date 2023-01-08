package app.sato.kchan.tasks

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.sato.kchan.tasks.databinding.DefaultNoticeActivityBinding
import java.util.*

class DefaultNoticeActivity: AppCompatActivity(){
    private lateinit var binding: DefaultNoticeActivityBinding
    var start = true // endならfalse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = DefaultNoticeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // トグルの値読み込みが必要
        binding.defaultNoticeSwitch.setOnCheckedChangeListener { _, isChenged ->
            if (isChenged) {
                // 変更を保存
                val calender = Calendar.getInstance()
                val m = calender.get(Calendar.MINUTE)+1
                binding.defaultStartTime.text = calender.get(Calendar.HOUR_OF_DAY).toString() + ":" + calender.get(Calendar.MINUTE)
                binding.defaultEndTime.text = calender.get(Calendar.HOUR_OF_DAY).toString() + ":" + m

                binding.defaultNoticeStartText.isVisible = true
                binding.defaultStartTime.isVisible = true
                binding.defaultNoticeEndText.isVisible = true
                binding.defaultEndTime.isVisible = true
            } else {
                // 変更を保存
                binding.defaultNoticeStartText.isVisible = false
                binding.defaultStartTime.isVisible = false
                binding.defaultNoticeEndText.isVisible = false
                binding.defaultEndTime.isVisible = false
            }
        }

        // 時間設定部分押した時の処理
        binding.defaultStartTime.setOnClickListener {
            start = true
            showTimePickerDialog()
        }

        binding.defaultEndTime.setOnClickListener {
            start = false
            showTimePickerDialog()
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
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // TimePickerの生成処理(触らなくていい)
    fun showTimePickerDialog() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            if (start) {
                binding.defaultStartTime.text = "$hour:$minute"
                // 開始時間の保存処理
            } else {
                binding.defaultEndTime.text = "$hour:$minute"
                // 終了時間の保存処理
            }
        }

        //タイムピッカーダイアログを生成および設定
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(this)).show()
    }

//    //デフォ開始
//    private fun showButton_onClick() {
//        //TimeActivityの時間とってくるやつ
//        Info.setDefaultNoticeShow()
//    }
//
//    //デフォ終了
//    private fun hideButton_onClick() {
//        //TimeActivityの時間とってくるやつ
//        Info.setDefaultNoticeHide()
//    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}