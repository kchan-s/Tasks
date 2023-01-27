package app.sato.kchan.tasks

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.sato.kchan.tasks.databinding.LocationActivityBinding
import app.sato.kchan.tasks.fanction.Location
import app.sato.kchan.tasks.fanction.LocationManager
import app.sato.kchan.tasks.fanction.NoteManager
import java.util.*


class LocationActivity: AppCompatActivity(){
    private lateinit var binding: LocationActivityBinding
//    var position = -1 // 初期値
    val locationNameData = mutableListOf("未選択", "Mapから選択")
    val locationData = mutableListOf<Location>()
    val lm = LocationManager()
    val nm = NoteManager()
    var address = ""
    var note = ""

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

        lm.search("")
        // よく行く場所に設定してある場所をとってくる
        for (i in 0 until lm.getLocationNumber()) {
            val copyLocationManager = lm.copy()
            copyLocationManager.select(i)
            val location = copyLocationManager.getLocation()!!
            if (location.isPermanent()) {
                locationNameData.add(locationNameData.lastIndex, location.getName().toString())
                locationData.add(location)
            }
        }

//        position = intent.getIntExtra("position", -1)
        note = intent.getStringExtra("note").toString()
        nm.receive(note)
        val n = nm.getNote()!!

        // ドロップダウンリスト関連処理
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationNameData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSettingSpinner.adapter = adapter
        binding.locationSettingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == locationNameData.lastIndex) {
                    binding.locationMapButton.isVisible = true
                } else if (pos != 0) {
                    nm.receive(note)
                    val n = nm.getNote()!!
                    n.setNoticeLocation(locationData[pos-1])
                } else {
                    nm.receive(note)
                    val n = nm.getNote()!!
                    n.setNoticeLocation(null)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 既に場所設定がされている場合の初期設定
        if (n.getNoticeLocation() != null) {
            val noteLocation = n.getNoticeLocation()!!
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
            mapButton_onClick()
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
                if (address != "") {
                    nm.receive(note)
                    val n = nm.getNote()!!
                    lm.search(address)
                    if (lm.isLocation()) n.setNoticeLocation(lm.getLocation())
                    else {
                        val location = lm.create()
                        val coordinate = doGeoCoding(address)
                        location.setAddress(address)
                        location.setName(binding.locationNameEdit.text.toString())
                        location.setLatitude(coordinate[0].latitude.toFloat())
                        location.setLongitude(coordinate[0].longitude.toFloat())
                        n.setNoticeLocation(location)
                    }
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun mapButton_onClick() {
        MapActivity.notice = true
        val map = Intent(this, MapActivity::class.java)
        map.putExtra("note", note)
        resultLauncher.launch(map)
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