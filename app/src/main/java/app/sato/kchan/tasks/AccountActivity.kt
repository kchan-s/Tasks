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
            accountChangeButtonOnClick()
        }

        // パスワード設定ボタンを押した時の処理
        binding.accountPasswordSettingButton.setOnClickListener {
            passwordButtonOnClick()
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
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //　ログイン画面遷移
    private fun accountChangeButtonOnClick() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    //　パスワード初期設定・変更画面遷移
    private fun passwordButtonOnClick() {
        val passwordInitializeSharedPreferences = getSharedPreferences("passwordInitialize", MODE_PRIVATE)
        val isPasswordInitialized = passwordInitializeSharedPreferences.getBoolean("passwordInitialize", true)
        if (isPasswordInitialized) {
            val passwordInitializeIntent = Intent(this, PasswordInitializeActivity::class.java)
            startActivity(passwordInitializeIntent)
        } else {
            val passwordChangeIntent = Intent(this, PasswordChangeActivity::class.java)
            startActivity(passwordChangeIntent)
        }
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}