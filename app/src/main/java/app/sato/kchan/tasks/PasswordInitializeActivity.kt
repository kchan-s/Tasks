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

class PasswordInitializeActivity: AppCompatActivity(){
    private lateinit var binding: PasswordInitializeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = PasswordInitializeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        binding.initializeVerification.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 8) {
                    binding.passwordInitializeNewLayout.error = "8文字以上入力してください"
                } else {
                    binding.passwordInitializeNewLayout.error = null
                }
            }
        })

        binding.initializeNew.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 8) {
                    binding.passwordInitializeVerificationLayout.error = "8文字以上入力してください"
                } else {
                    binding.passwordInitializeVerificationLayout.error = null
                }
            }
        })

        // 設定完了ボタンタップ処理
        binding.InitializeDoneButton.setOnClickListener {
            val question1 = binding.question1.selectedItemPosition
            val question2 = binding.question2.selectedItemPosition
            val question3 = binding.question3.selectedItemPosition
            val answer1 = binding.answer1.text.toString()
            val answer2 = binding.answer2.text.toString()
            val answer3 = binding.answer3.text.toString()
            val password = binding.initializeNew.text.toString()
            val verification = binding.initializeVerification.text.toString()

            // 質問が選択されていない場合の場合分けも行う
            when {
                question1 == question2 || question1 == question3 || question2 == question3 ->{
                    Toast.makeText(this, "秘密の質問は異なるものにしてください", Toast.LENGTH_LONG).show()
                }
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

        val question = listOf("はじめて飼ったペットの名前は？",
                "一番年上のいとこの名前は？",
                "母親の旧姓は？",
                "両親が出会った街は？",
                "卒業した小学校は？",
                "初めて買ったCDは？",
                "初めて買った車は？")
        val adapter = ArrayAdapter(this, R.layout.spinner, question)

        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.question1.adapter = adapter
        binding.question2.adapter = adapter
        binding.question2.setSelection(1)
        binding.question2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (binding.question2.selectedItemPosition == binding.question1.selectedItemPosition) {
                    Toast.makeText(this@PasswordInitializeActivity, "別の質問を選択してください", Toast.LENGTH_LONG).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.question3.adapter = adapter
        binding.question3.setSelection(2)
        binding.question3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (binding.question3.selectedItemPosition == binding.question1.selectedItemPosition) {
                    Toast.makeText(this@PasswordInitializeActivity, "別の質問を選択してください", Toast.LENGTH_LONG).show()
                } else if (binding.question3.selectedItemPosition == binding.question2.selectedItemPosition) {
                    Toast.makeText(this@PasswordInitializeActivity, "別の質問を選択してください", Toast.LENGTH_LONG).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}