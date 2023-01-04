package app.sato.kchan.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.TimeActivityBinding
import java.util.*

class TimeActivity: AppCompatActivity(){
    private lateinit var binding: TimeActivityBinding
    var setting = true // 時間通知設定のトグル代わり

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // トグルがオンになっているかどうか判定
        if (setting) {
            // ↓showButton_onClick()
            binding.start.setOnClickListener {
                showDatePickerDialog()
                //開始時刻に設定？
            }

            // ↓hideButton_onClick()
            binding.end.setOnClickListener {
                showDatePickerDialog()
                //終了時間に設定？
            }
        }else{
            //del (time and date)settingが必要
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

    // 日付選択のダイアログ生成
    fun showDatePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()

        //日付ピッカーダイアログを生成および設定
        DatePickerDialog(
            this,
            //ダイアログのクリックイベント設定
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val currentDate =
                    Calendar.getInstance().apply { set(year, monthOfYear, dayOfMonth) }

                showTimePickerDialog()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
        }.show()
    }

    // 時間選択のダイアログ生成
    fun showTimePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()

        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
        }

        //タイムピッカーダイアログを生成および設定
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }
}