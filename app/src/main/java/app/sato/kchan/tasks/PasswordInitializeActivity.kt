package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.sato.kchan.tasks.databinding.PasswordInitializeActivityBinding
import app.sato.kchan.tasks.fanction.Account

class PasswordInitializeActivity: AppCompatActivity(){
    private lateinit var binding: PasswordInitializeActivityBinding
    val account = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = PasswordInitializeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        binding.passwordInitializeSettingEdit.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (p0.length < 8) {
                    binding.passwordInitializeSettingLayout.error = "8文字以上入力してください"
                } else if (p0.length > 50) {
                    binding.passwordInitializeSettingLayout.error = "50文字以下で設定してください"
                } else {
                    binding.passwordInitializeSettingLayout.error = null
                }
            }
        })

        binding.passwordInitializeVerificationEdit.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (p0.length < 8) {
                    binding.passwordInitializeVerificationLayout.error = "8文字以上入力してください"
                } else if (p0.length > 50) {
                    binding.passwordInitializeVerificationLayout.error = "50文字以下で設定してください"
                } else {
                    binding.passwordInitializeVerificationLayout.error = null
                }
            }
        })

        // 設定完了ボタンタップ処理
        binding.passwordInitializeDoneButton.setOnClickListener {
            changeButton_onClick()
        }

        // ドロップダウンリストの設定
        val question = listOf("未選択",
                "はじめて飼ったペットの名前は？",
                "一番年上のいとこの名前は？",
                "母親の旧姓は？",
                "両親が出会った街は？",
                "卒業した小学校は？",
                "初めて買ったCDは？",
                "初めて買った車は？")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, question)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.passwordInitializeQuestion1Spinner.adapter = adapter
        binding.passwordInitializeQuestion2Spinner.adapter = adapter
        binding.passwordInitializeQuestion2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (binding.passwordInitializeQuestion2Spinner.selectedItemPosition != 0 &&
                    binding.passwordInitializeQuestion2Spinner.selectedItemPosition == binding.passwordInitializeQuestion1Spinner.selectedItemPosition) {
                    Toast.makeText(this@PasswordInitializeActivity, "別の質問を選択してください", Toast.LENGTH_LONG).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.passwordInitializeQuestion3Spinner.adapter = adapter
        binding.passwordInitializeQuestion3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (binding.passwordInitializeQuestion3Spinner.selectedItemPosition != 0 &&
                    binding.passwordInitializeQuestion3Spinner.selectedItemPosition == binding.passwordInitializeQuestion1Spinner.selectedItemPosition) {
                    Toast.makeText(this@PasswordInitializeActivity, "別の質問を選択してください", Toast.LENGTH_LONG).show()
                } else if (binding.passwordInitializeQuestion3Spinner.selectedItemPosition != 0 &&
                    binding.passwordInitializeQuestion3Spinner.selectedItemPosition == binding.passwordInitializeQuestion2Spinner.selectedItemPosition) {
                    Toast.makeText(this@PasswordInitializeActivity, "別の質問を選択してください", Toast.LENGTH_LONG).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val toolbar = binding.passwordInitializeToolbar
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

    private fun changeButton_onClick() {
        val question1 = binding.passwordInitializeQuestion1Spinner.selectedItemPosition
        val question2 = binding.passwordInitializeQuestion2Spinner.selectedItemPosition
        val question3 = binding.passwordInitializeQuestion3Spinner.selectedItemPosition
        val answer1 = binding.passwordInitializeAnswer1Edit.text.toString()
        val answer2 = binding.passwordInitializeAnswer2Edit.text.toString()
        val answer3 = binding.passwordInitializeAnswer3Edit.text.toString()
        val password = binding.passwordInitializeSettingEdit.text.toString()
        val verification = binding.passwordInitializeVerificationEdit.text.toString()

        // 質問が選択されていない場合の場合分けも行う
        when {
            question1 == 0 || question2 == 0 || question3 == 0 -> {
                Toast.makeText(this, "秘密の質問を選択してください", Toast.LENGTH_LONG).show()
            }
            question1 == question2 || question1 == question3 || question2 == question3 -> {
                Toast.makeText(this, "秘密の質問は異なるものにしてください", Toast.LENGTH_LONG).show()
            }
            answer1 != "" && answer2 != "" && answer3 != "" && password.length >= 8 && verification.length >= 8 -> {
                if (password == verification) {
                    // 保存
                    account.setPassword(password)
                    // パスワードが登録完了したことを保存
                    val aPreferences = getSharedPreferences("passwordInitialize", MODE_PRIVATE)
                    val aEditor = aPreferences.edit()
                    aEditor.putBoolean("passwordInitialize", false)
                    aEditor.commit()

                    Toast.makeText(this, "パスワード設定が完了しました", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "新たなパスワードが一致しません", Toast.LENGTH_LONG).show()
                }
            }
            password.length > 50 || verification.length > 50 -> {
                Toast.makeText(this, "パスワードは50文字以下で設定してください", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "必要項目を全て入力してください", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}