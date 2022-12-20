package app.sato.kchan.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.DeleteActivityBinding

class DeleteActivity: AppCompatActivity() {
    private lateinit var binding: DeleteActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DeleteActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.delete_menu, menu)
        return true
    }
}