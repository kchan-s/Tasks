package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.LocationStockRegisterActivityBinding

class LocationStockRegisterActivity: AppCompatActivity(){
    private lateinit var binding: LocationStockRegisterActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = LocationStockRegisterActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        binding.mapButton.setOnClickListener {
            val map = Intent(this, MapsActivity::class.java)
            startActivity(map)
        }

        binding.doneButton.setOnClickListener {
            // 保存処理
            val locationName = binding.locationName.text.toString()
            val location = binding.location.text.toString()

            LocationStockAdapter.locationData[LocationStockAdapter.locationData.lastIndex][0] = locationName

            if (locationName != "" && location != "") {
                // 保存処理
                val intent = Intent(this, LocationStockActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "必要項目を全て埋めてください", Toast.LENGTH_LONG).show()
            }
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