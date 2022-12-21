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

        binding.accountSwitch.setOnClickListener {
            val login = Intent(this, LoginActivity::class.java)
            startActivity(login)
        }

        binding.passwordSetting.setOnClickListener {
            // initialize = false
            if (initialize) {
                val passIni = Intent(this, PasswordInitializeActivity::class.java)
                startActivity(passIni)
            } else {
                val passCha = Intent(this, PasswordChangeActivity::class.java)
                startActivity(passCha)
            }
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}