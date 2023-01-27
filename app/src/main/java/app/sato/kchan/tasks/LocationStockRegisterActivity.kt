package app.sato.kchan.tasks

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

        binding.locationStockRegisterMapButton.setOnClickListener {
            mapButton_onClick()
        }

        binding.locationStockRegisterDoneButton.setOnClickListener {
            registerButton_onClick()
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

    private fun mapButton_onClick() {
        val map = Intent(this, MapActivity::class.java)
        resultLauncher.launch(map)
    }

    private fun registerButton_onClick() {
        val locationName = binding.locationStockRegisterNameEdit.text.toString()
        val locationAddress = binding.locationStockRegisterAddressEdit.text.toString()

        val lm = LocationManager()
        if (locationName != "" && locationAddress != "") {
            val l = lm.create()
            val coordinate: MutableList<Address>
            if (address != "") coordinate = doGeoCoding(address)
            else coordinate = doGeoCoding(binding.locationStockRegisterAddressEdit.text.toString())
            l.setName(locationName)
            l.setAddress(locationAddress)
            l.setLongitude(coordinate[0].latitude.toFloat())
            l.setLongitude(coordinate[0].longitude.toFloat())
            l.setPermanent()
            finish()
        } else {
            Toast.makeText(this, "必要項目を全て埋めてください", Toast.LENGTH_LONG).show()
        }
    }

    private fun doGeoCoding(query: String): MutableList<Address> {
        val gcoder = Geocoder(this, Locale.getDefault())
        return gcoder.getFromLocationName(query, 1)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}