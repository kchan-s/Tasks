package app.sato.kchan.tasks

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.LocationActivityBinding

class LocationActivity: AppCompatActivity(){
    private lateinit var binding: LocationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

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