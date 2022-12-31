package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.AccountActivityBinding

class AccountActivity: AppCompatActivity(){
    private lateinit var binding: AccountActivityBinding
    private var initialize = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // アカウント切り替えボタンを押した時の処理
        binding.accountSwitch.setOnClickListener {
            val login = Intent(this, LoginActivity::class.java)
            startActivity(login)
        }

        // パスワード設定ボタンを押した時の処理
        binding.passwordSetting.setOnClickListener {
            initialize = false
            // パスワードが初期設定か判定、遷移先を変える
            if (initialize) {
                val passIni = Intent(this, PasswordInitializeActivity::class.java)
                startActivity(passIni)
            } else {
                val passCha = Intent(this, PasswordChangeActivity::class.java)
                startActivity(passCha)
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
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}