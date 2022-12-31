package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.PasswordChangeActivityBinding

class PasswordChangeActivity: AppCompatActivity(){
    private lateinit var binding: PasswordChangeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PasswordChangeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // パスワードを忘れた方はこちらボタンタップ処理
        binding.passReset.setOnClickListener {
            val reset = Intent(this, PasswordResetActivity::class.java)
            startActivity(reset)
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
}