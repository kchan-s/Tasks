package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // ドロップダウンリストの設定、場合分け
        var location_list = listOf("未選択", "Mapを表示") // 仮置き
        val adapter = ArrayAdapter(this, R.layout.spinner, location_list)

        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.locationSpinner.adapter = adapter
        binding.locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == 1) {
                    binding.mapButton.isVisible = true
                } else {
                    // 場所設定
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // map表示ボタンクリック時の処理
        binding.mapButton.setOnClickListener {
            val map = Intent(this, MapsActivity::class.java)
            startActivity(map)
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

}