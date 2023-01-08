package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.PasswordChangeActivityBinding

class PasswordChangeActivity: AppCompatActivity(){
    private lateinit var binding: PasswordChangeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = PasswordChangeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // パスワードを忘れた方はこちらボタンタップ処理
        binding.passReset.setOnClickListener {
            val reset = Intent(this, PasswordResetActivity::class.java)
            startActivity(reset)
        }

        // 設定完了ボタンタップ処理
        binding.passDone.setOnClickListener {
            val nowPassword = binding.changeNow.text.toString()
            val newPassword = binding.changeNew.text.toString()
            val verification = binding.changeVerification.text.toString()
            if (nowPassword.length >= 8 && newPassword.length >= 8 && verification.length >= 8) {
                // 今のパスワードがあっているか判定が必要
                when {
                    // ↓のnowPasswordは読み込んできたやつにする
                    nowPassword == newPassword -> {
                        Toast.makeText(this, "新しいパスワードが現在のパスワードと同じです", Toast.LENGTH_LONG).show()
                    }
                    newPassword == verification -> {
                        // 保存
                        val intent = Intent(this, AccountActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        Toast.makeText(this, "新たなパスワードが一致しません", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "必要項目を全て入力してください", Toast.LENGTH_LONG).show()
            }

        }

        binding.changeNow.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 8) {
                    binding.passwordChangeNowLayout.error = "8文字以上入力してください"
                } else {
                    binding.passwordChangeNowLayout.error = null
                }
            }
        })

        binding.changeNew.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 8) {
                    binding.passwordChangeNewLayout.error = "8文字以上入力してください"
                } else {
                    binding.passwordChangeNewLayout.error = null
                }
            }
        })

        binding.changeVerification.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 8) {
                    binding.passwordChangeVerificationLayout.error = "8文字以上入力してください"
                } else {
                    binding.passwordChangeVerificationLayout.error = null
                }
            }
        })

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

    //設定完了 画面遷移　アカウントへ
//    private fun changeButton_onClick() {
//
//    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}