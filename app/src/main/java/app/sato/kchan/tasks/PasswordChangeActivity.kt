package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.PasswordChangeActivityBinding

class PasswordChangeActivity: AppCompatActivity(){
    private lateinit var binding: PasswordChangeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PasswordChangeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        binding.passReset.setOnClickListener {
            val reset = Intent(this, PasswordResetActivity::class.java)
            startActivity(reset)
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}