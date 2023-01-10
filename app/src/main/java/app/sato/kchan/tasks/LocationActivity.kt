package app.sato.kchan.tasks

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.sato.kchan.tasks.databinding.LocationActivityBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class LocationActivity: AppCompatActivity(){
    private lateinit var binding: LocationActivityBinding
    var position = -1 // 初期値
    val locationNameList = LocationStockAdapter.locationNameData
    val locationCoordinateList = LocationStockAdapter.locationCoordinateData

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = LocationActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        position = intent.getIntExtra("position", -1)

        // ドロップダウンリストの設定、場合分け

        val adapter = ArrayAdapter(this, R.layout.spinner, locationNameList)

        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.locationSpinner.adapter = adapter
        binding.locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == locationNameList.lastIndex) {
                    binding.mapButton.isVisible = true
                } else if (pos != 0) {
                    // 場所設定
                    HomeMemoListAdapter.settingData[position].clear()
                    HomeMemoListAdapter.settingData[position].addAll(mutableListOf("3", locationNameList[pos], pos.toString(), locationCoordinateList[pos-1][0].toString(), locationCoordinateList[pos-1][1].toString()))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // map表示ボタンクリック時の処理
        binding.mapButton.setOnClickListener {
            val map = Intent(this, MapsActivity::class.java)
            map.putExtra("position", position)
            startActivity(map)
        }

        if (HomeMemoListAdapter.settingData[position][0] == "3") {
            binding.locationSpinner.setSelection(HomeMemoListAdapter.settingData[position][2].toInt())
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        if (HomeMemoListAdapter.settingData[position][0] == "4") {
            binding.locationNameEdit.isVisible = true
            binding.locationNameEdit.setText(HomeMemoListAdapter.settingData[position][1])
            binding.locationSpinner.setSelection(locationNameList.lastIndex)
        }
    }

     // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                if (HomeMemoListAdapter.settingData[position][0] == "4") {
                    HomeMemoListAdapter.settingData[position][1] = binding.locationNameEdit.text.toString()
                }
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