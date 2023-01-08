package app.sato.kchan.tasks

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.LoginActivityBinding

class LoginActivity: AppCompatActivity(){
    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = LoginActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val filter = InputFilter { source, _, _, _, _, _ ->
            if (source.matches("^[a-zA-Z0-9]+$".toRegex())) source else ""
        }

        if (filter !in binding.accountIdLogin.filters) binding.accountIdLogin.filters = arrayOf(*binding.accountIdLogin.filters, filter)

        binding.accountIdLogin.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //処理
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 6) {
                    binding.loginAccountIdLayout.error = "アカウントIDは6文字です"
                } else {
                    binding.loginAccountIdLayout.error = null
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //処理
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

    //ログイン

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}