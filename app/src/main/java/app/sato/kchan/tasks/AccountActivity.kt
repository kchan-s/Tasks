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
            accountChangeButton_onClick()
        }

        // パスワード設定ボタンを押した時の処理
        binding.passwordSetting.setOnClickListener {
            passwordButton_onClick()
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

    //画面遷移　ログインへ
    private fun accountChangeButton_onClick() {
        val login = Intent(this, LoginActivity::class.java)
        startActivity(login)
    }

    //画面遷移　パスワード　設定OR初期設定
    private fun passwordButton_onClick() {
        initialize = false  //仮かこれでいいのか？
        // パスワードが初期設定か判定し、遷移先を変える
        if (initialize) {
            val passIni = Intent(this, PasswordInitializeActivity::class.java)
            startActivity(passIni)
        } else {
            val passCha = Intent(this, PasswordChangeActivity::class.java)
            startActivity(passCha)
        }
    }
}