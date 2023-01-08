package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.PasswordResetActivityBinding

class PasswordResetActivity: AppCompatActivity(){
    private lateinit var binding: PasswordResetActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = PasswordResetActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // 質問を取得して表示する処理が必要

        // 設定完了ボタンタップ処理
        binding.ResetDoneButton.setOnClickListener {
            val answer1 = binding.answer1.text.toString()
            val answer2 = binding.answer2.text.toString()
            val answer3 = binding.answer3.text.toString()
            val password = binding.resetNew.text.toString()
            val verification = binding.resetVerification.text.toString()

            // 質問が選択されていない場合の場合分けも行う
            when {
                answer1 != "" && answer2 != "" && answer3 != "" && password.length >= 8 && verification.length >= 8 -> {
                    if (password == verification) {
                        // 保存

                        // パスワードが登録完了したことを保存
                        val aPreferences = getSharedPreferences("passwordInitialize", MODE_PRIVATE)
                        val aEditor = aPreferences.edit()
                        aEditor.putBoolean("passwordInitialize", false)
                        aEditor.commit()

                        val intent = Intent(this, AccountActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "新たなパスワードが一致しません", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    Toast.makeText(this, "必要項目を全て入力してください", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.resetNew.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 8) {
                    binding.passwordResetNewLayout.error = "8文字以上入力してください"
                } else {
                    binding.passwordResetNewLayout.error = null
                }
            }
        })

        binding.resetVerification.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 8) {
                    binding.passwordResetVerificationLayout.error = "8文字以上入力してください"
                } else {
                    binding.passwordResetVerificationLayout.error = null
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
//        Account.resetPassword()
//    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}