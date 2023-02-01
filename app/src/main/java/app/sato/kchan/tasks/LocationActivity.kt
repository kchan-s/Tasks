package app.sato.kchan.tasks

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import app.sato.kchan.tasks.HomeActivity.Companion.context
import app.sato.kchan.tasks.databinding.LocationActivityBinding
import app.sato.kchan.tasks.fanction.Location
import app.sato.kchan.tasks.fanction.LocationManager
import app.sato.kchan.tasks.fanction.NoteManager
import app.sato.kchan.tasks.fanction.NoticeManager
import java.util.*


class LocationActivity: AppCompatActivity(){
    private lateinit var binding: LocationActivityBinding
    val locationNameData = mutableListOf("未選択", "Mapから選択")
    val locationData = mutableListOf<Location>()
    val locationManager = LocationManager()
    val noteManager = NoteManager()
    var address = ""
    var received = ""

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            address = result.data?.getStringExtra(MapActivity.ADDRESS_RESULT).toString()
        }
    }

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = LocationActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        locationManager.search("")
        // よく行く場所に設定してある場所をとってくる
        for (i in 0 until locationManager.getLocationNumber()) {
            val copyLocationManager = locationManager.copy()
            copyLocationManager.select(i)
            val location = copyLocationManager.getLocation()!!
            if (location.isPermanent()) {
                locationNameData.add(locationNameData.lastIndex, location.getName().toString())
                locationData.add(location)
            }
        }

        received = intent.getStringExtra("received").toString()
        noteManager.receive(received)
        val receivedNote = noteManager.getNote()!!

        // ドロップダウンリスト関連処理
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationNameData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSettingSpinner.adapter = adapter
        binding.locationSettingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == locationNameData.lastIndex) {
                    binding.locationMapButton.isVisible = true
                } else if (pos != 0) {
                    receivedNote.setNoticeLocation(locationData[pos-1])
                } else {
                    receivedNote.setNoticeLocation(null)
                }
                val targetIntent = Intent(context, ForegroundNotificationService::class.java)
                context.stopService(targetIntent)
                context.startForegroundService(targetIntent)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 既に場所設定がされている場合の初期設定
        if (receivedNote.getNoticeLocation() != null) {
            val noteLocation = receivedNote.getNoticeLocation()!!
            if (noteLocation.isPermanent()) {
                for (i in 0 until locationData.size) {
                    if (locationData[i].getName() == noteLocation.getName()
                        && locationData[i].getAddress() == noteLocation.getAddress()) {
                        binding.locationSettingSpinner.setSelection(i+1)
                    }
                }
            } else {
                binding.locationAddress.isVisible = true
                binding.locationNameEdit.isVisible = true
                binding.locationAddress.text = noteLocation.getAddress()
                binding.locationNameEdit.setText(noteLocation.getName())
                binding.locationSettingSpinner.setSelection(locationNameData.lastIndex)
            }
        }

        // map表示ボタンクリック時の処理
        binding.locationMapButton.setOnClickListener {
            mapButtonOnClick()
        }

        val toolbar = binding.locationToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        if (address != "") {
            binding.locationNameEdit.isVisible = true
            binding.locationAddress.isVisible = true
            binding.locationAddress.text = address
        }
    }

     // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                noteManager.receive(received)
                val note = noteManager.getNote()!!
                val targetIntent = Intent(context, ForegroundNotificationService::class.java)
                context.stopService(targetIntent)
                context.startForegroundService(targetIntent)

                if (address != "") {
                    locationManager.search(address)
                    if (locationManager.isLocation()) note.setNoticeLocation(locationManager.getLocation())
                    else {
                        val location = locationManager.create()
                        val coordinate = doGeoCoding(address)
                        location.setAddress(address)
                        location.setName(binding.locationNameEdit.text.toString())
                        location.setLatitude(coordinate[0].latitude.toFloat())
                        location.setLongitude(coordinate[0].longitude.toFloat())
                        note.setNoticeLocation(location)
                    }
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun mapButtonOnClick() {
        MapActivity.notice = true
        val mapIntent = Intent(this, MapActivity::class.java)
        mapIntent.putExtra("received", received)
        resultLauncher.launch(mapIntent)
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