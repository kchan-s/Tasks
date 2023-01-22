package app.sato.kchan.tasks

import android.content.Intent
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


class LocationActivity: AppCompatActivity(){
    private lateinit var binding: LocationActivityBinding
    var position = -1 // 初期値
    val locationData = mutableListOf("未選択", "Mapから選択")
    val idData = mutableListOf<String>()
    val lm = LocationManager()
    val nm = NoteManager()
    var address: String? = ""

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

        // よく行く場所に設定してある場所をとってくる
        for (i in 0 .. lm.getLocationNumber()) {
            lm.selectByTempId(i.toString())
            val location = lm.getLocation()
            if (location.isPermanent()) {
                locationData.add(1, location.getName())
                idData.add(i.toString())
            }
        }

        position = intent.getIntExtra("position", -1)
        nm.selectByTempId(position.toString())
        val n = nm.getNote()

        // ドロップダウンリスト関連処理
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSettingSpinner.adapter = adapter
        binding.locationSettingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == locationData.lastIndex) {
                    binding.locationMapButton.isVisible = true
                } else if (pos != 0) {
                    val lm = LocationManager()
                    lm.selectByTempId(idData[pos-1])
                    n.setNoticeLocation(lm.getLocation())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 既に場所設定がされている場合の初期設定
        if (n.getNoticeLocation() != null) {
            val noteLocation = n.getNoticeLocation()
            for (i in 1 .. locationData.size) {
                if (noteLocation!!.getName() == locationData[i])
                    binding.locationSettingSpinner.setSelection(i)
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

     // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                if (address != "") {
                    nm.selectByTempId(position.toString())
                    val n = nm.getNote()
                    lm.search(address.toString())
                    if (lm.isLocation()) n.setNoticeLocation(lm.getLocation())
                    else {
                        val l = lm.create()
                        l.setAddress(address.toString())
                        l.setName(binding.locationNameEdit.text.toString())
                    }
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun mapButton_onClick() {
        val map = Intent(this, MapActivity::class.java)
        map.putExtra("position", position)
        resultLauncher.launch(map)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}