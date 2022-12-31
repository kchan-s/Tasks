package app.sato.kchan.tasks

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.DefaultNoticeActivityBinding
import java.util.*

class DefaultNoticeActivity: AppCompatActivity(){
    private lateinit var binding: DefaultNoticeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DefaultNoticeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // 時間設定部分押した時の処理(中身は変更予定)
        binding.editTextTime.setOnClickListener {
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