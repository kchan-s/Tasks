package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.PasswordResetActivityBinding
import app.sato.kchan.tasks.fanction.Account

class PasswordResetActivity: AppCompatActivity(){
    private lateinit var binding: PasswordResetActivityBinding
    val account = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = PasswordResetActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // 質問を取得して表示する処理が必要

        // 設定完了ボタンタップ処理
        binding.passwordResetDoneButton.setOnClickListener {
            changeButtonOnClick()
        }

        binding.passwordResetNewEdit.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                when {
                    p0.length < 8 -> {
                        binding.passwordResetNewLayout.error = "8文字以上入力してください"
                    }
                    p0.length >= 50 -> {
                        binding.passwordResetNewLayout.error = "50文字以下で設定してください"
                    }
                    else -> {
                        binding.passwordResetNewLayout.error = null
                    }
                }
            }
        })

        binding.passwordResetVerificationEdit.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                when {
                    p0.length < 8 -> {
                        binding.passwordResetVerificationLayout.error = "8文字以上入力してください"
                    }
                    p0.length >= 50 -> {
                        binding.passwordResetVerificationLayout.error = "50文字以下で設定してください"
                    }
                    else -> {
                        binding.passwordResetVerificationLayout.error = null
                    }
                }
            }
        })

        val toolbar = binding.passwordResetToolbar
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

    private fun changeButtonOnClick() {
        val answer1 = binding.passwordResetAnswer1Edit.text.toString()
        val answer2 = binding.passwordResetAnswer2Edit.text.toString()
        val answer3 = binding.passwordResetAnswer3Edit.text.toString()
        val password = binding.passwordResetNewEdit.text.toString()
        val verification = binding.passwordResetVerificationEdit.text.toString()

        // 質問が選択されていない場合の場合分けも行う
        if (answer1 != "" && answer2 != "" && answer3 != "" && password.length >= 8 && verification.length >= 8) {
            when {
                password.length > 50 || verification.length > 50 -> {
                    Toast.makeText(this, "パスワードは50文字以下で設定してください", Toast.LENGTH_LONG).show()
                }
                password == verification -> {
                    account.resetPassword()
                    Toast.makeText(this, "パスワード再設定が完了しました", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    Toast.makeText(this, "新たなパスワードが一致しません", Toast.LENGTH_LONG).show()
                }
            }
        }
        else {
            Toast.makeText(this, "必要項目を全て入力してください", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}