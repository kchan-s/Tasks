package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.PasswordResetActivityBinding

class PasswordResetActivity: AppCompatActivity(){
    private lateinit var binding: PasswordResetActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PasswordResetActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // 設定完了ボタンタップ処理
        binding.ResetDoneButton.setOnClickListener {
            // 保存
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
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

//    //設定完了 画面遷移　アカウントへ
//    private fun changeButton_onClick() {
//
//    }
}