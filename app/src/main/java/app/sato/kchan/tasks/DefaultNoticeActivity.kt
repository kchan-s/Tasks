package app.sato.kchan.tasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.DefaultNoticeActivityBinding

class DefaultNoticeActivity: AppCompatActivity(){
    private lateinit var binding: DefaultNoticeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DefaultNoticeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}