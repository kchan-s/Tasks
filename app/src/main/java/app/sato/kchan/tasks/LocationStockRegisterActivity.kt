package app.sato.kchan.tasks

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.sato.kchan.tasks.databinding.LocationStockRegisterActivityBinding
import app.sato.kchan.tasks.fanction.LocationManager
import java.util.*


class LocationStockRegisterActivity: AppCompatActivity(){
    private lateinit var binding: LocationStockRegisterActivityBinding
    var address = ""
    val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            address = result.data?.getStringExtra(MapActivity.ADDRESS_RESULT).toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = LocationStockRegisterActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        binding.locationStockRegisterMapButton.setOnClickListener {
            mapButtonOnClick()
        }

        binding.locationStockRegisterDoneButton.setOnClickListener {
            registerButtonOnClick()
        }

        val toolbar = binding.locationStockRegisterToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        binding.locationStockRegisterAddressEdit.setText(address)
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

    private fun mapButtonOnClick() {
        val mapIntent = Intent(this, MapActivity::class.java)
        resultLauncher.launch(mapIntent)
    }

    private fun registerButtonOnClick() {
        val locationName = binding.locationStockRegisterNameEdit.text.toString()
        val locationAddress = binding.locationStockRegisterAddressEdit.text.toString()

        val locationManager = LocationManager()
        if (locationName != "" && locationAddress != "") {
            val location = locationManager.create()
            val coordinate = if (address != "") doGeoCoding(address)
            else doGeoCoding(binding.locationStockRegisterAddressEdit.text.toString())
            location.setName(locationName)
            location.setAddress(locationAddress)
            location.setLatitude(coordinate[0].latitude.toFloat())
            location.setLongitude(coordinate[0].longitude.toFloat())
            location.setPermanent()
            finish()
        } else {
            Toast.makeText(this, "必要項目を全て埋めてください", Toast.LENGTH_LONG).show()
        }
    }

    private fun doGeoCoding(query: String): MutableList<Address> {
        val gCoder = Geocoder(this, Locale.getDefault())
        return gCoder.getFromLocationName(query, 1)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_DayNight))
    }
}