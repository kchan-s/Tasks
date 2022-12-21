package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.EditActivityBinding


class EditActivity : AppCompatActivity() {
    private lateinit var binding: EditActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lock -> {
                return true
            }
            R.id.menu_delete -> {
                return true
            }
            R.id.menu_time -> {
                intent = Intent(this, TimeActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_spot -> {
                intent = Intent(this, LocationActivity::class.java)
                startActivity(intent)
                return true
            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}