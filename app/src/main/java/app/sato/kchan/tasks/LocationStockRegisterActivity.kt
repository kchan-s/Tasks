package app.sato.kchan.tasks

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.LocationStockRegisterActivityBinding
import java.util.*

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

            if (locationName != "" && location != "") {
                val addr = doGeoCoding(location)
                LocationStockAdapter.locationNameData.add(locationName)
                LocationStockAdapter.locationCoordinateData.add(listOf(addr[0].latitude, addr[0].longitude))
                finish()
            } else {
                Toast.makeText(this, "必要項目を全て埋めてください", Toast.LENGTH_LONG).show()
            }
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        val lData = LocationStockAdapter.locationCoordinateData[LocationStockAdapter.locationCoordinateData.lastIndex]
        val addressLine = doReverseGeoCoding(lData[0], lData[1]).get(0).getAddressLine(0).toString()
        val addr = addressLine.split(" ")
        binding.location.setText(addr[1])
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

    private fun doGeoCoding(query: String): MutableList<Address> {
        val gcoder = Geocoder(this, Locale.getDefault())
        return gcoder.getFromLocationName(query, 1)
    }

    private fun doReverseGeoCoding(lat: Double, lng: Double) : MutableList<Address>{
        val gcoder = Geocoder(this, Locale.getDefault())
        return gcoder.getFromLocation(lat, lng, 1)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}