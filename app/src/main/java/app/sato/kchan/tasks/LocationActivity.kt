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
//    var position = -1 // 初期値
    val locationData = mutableListOf("未選択", "Mapから選択")
    val idData = mutableListOf<String>()
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
        for (i in 1 .. lm.getLocationNumber()) {
            lm.selectByTempId(i.toString())
            val location = lm.getLocation()!!
            if (location.isPermanent()) {
                locationData.add(1, location.getName().toString())
                idData.add(i.toString())
            }
        }

//        position = intent.getIntExtra("position", -1)
        note = intent.getStringExtra("note").toString()
        nm.receive(note)
        val n = nm.getNote()!!

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
                println(note)
                if (address != "") {
                    nm.receive(note)
                    val n = nm.getNote()!!
                    lm.search(address)
                    if (lm.isLocation()) n.setNoticeLocation(lm.getLocation())
                    else {
                        val l = lm.create()
                        l.setAddress(address)
                        l.setName(binding.locationNameEdit.text.toString())
                        n.setNoticeLocation(lm.getLocation())
                    }
                    finish()
                }
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

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}