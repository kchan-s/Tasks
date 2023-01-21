package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.AccountActivityBinding
import app.sato.kchan.tasks.fanction.Account

class AccountActivity: AppCompatActivity(){
    private lateinit var binding: AccountActivityBinding
    val account = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = AccountActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        binding.loginIdText.text = account.getId()

        // アカウント切り替えボタンを押した時の処理
        binding.accountChangeButton.setOnClickListener {
            accountChangeButton_onClick()
        }

        // パスワード設定ボタンを押した時の処理
        binding.accountPasswordSettingButton.setOnClickListener {
            passwordButton_onClick()
        }

        val toolbar = binding.accountToolbar
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
        val aPreferences = getSharedPreferences("passwordInitialize", MODE_PRIVATE)
        val passwordInitialize = aPreferences.getBoolean("passwordInitialize", true)
        if (passwordInitialize) {
            val passIni = Intent(this, PasswordInitializeActivity::class.java)
            startActivity(passIni)
        } else {
            val passCha = Intent(this, PasswordChangeActivity::class.java)
            startActivity(passCha)
        }
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}