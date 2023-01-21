package app.sato.kchan.tasks

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.LoginActivityBinding
import app.sato.kchan.tasks.fanction.Account
import com.google.android.material.textfield.TextInputLayout

class LoginActivity: AppCompatActivity(){
    private lateinit var binding: LoginActivityBinding
    val account = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = LoginActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        binding.loginIdEdit.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (p0.length < 6) {
                    binding.loginPasswordIdLayout.error = "アカウントIDは6文字です"
                } else {
                    binding.loginPasswordIdLayout.error = null
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.loginPasswordEdit.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (p0.length < 6) {
                    binding.loginPassword.error = "8文字以上入力してください"
                } else if (p0.length > 50) {
                    binding.loginPassword.error = "50文字以下で設定してください"
                } else {
                    binding.loginPassword.error = null
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.loginDoneButton.setOnClickListener {
            loginButton_onClick()
        }

        val toolbar = binding.loginToolbar
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

    //ログイン
    private fun loginButton_onClick() {
        if (binding.loginIdEdit.text.toString() != "" &&  binding.loginPasswordEdit.text.toString() != "") {
            val login = account.login(binding.loginIdEdit.text.toString(), binding.loginPasswordEdit.text.toString())
            if (login) finish()
            else Toast.makeText(this, "ログインIDまたはパスワードが間違っています", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "必要項目を全て入力してください", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}
