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


class LocationActivity: AppCompatActivity(){
    private lateinit var binding: LocationActivityBinding
    val locationNameList = LocationStockAdapter.locationNameData
    val locationCoordinateList = LocationStockAdapter.locationCoordinateData
    var position = -1 // 初期値
    lateinit var notificationList: MutableList<String>

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = LocationActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        position = intent.getIntExtra("position", -1)
        if (position == -1) {
            HomeMemoListAdapter.notificationSettingData.add(mutableListOf(""))
            position = HomeMemoListAdapter.notificationSettingData.lastIndex
        }

        notificationList = HomeMemoListAdapter.notificationSettingData[position]

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationNameList)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSettingSpinner.adapter = adapter
        binding.locationSettingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == locationNameList.lastIndex) {
                    binding.locationMapButton.isVisible = true
                } else if (pos != 0) {
                    // 場所設定
                    notificationList.clear()
                    notificationList.addAll(
                        mutableListOf(
                            "3",
                            locationNameList[pos],
                            pos.toString(),
                            locationCoordinateList[pos-1][0].toString(),
                            locationCoordinateList[pos-1][1].toString()
                        )
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (notificationList[0] == "3") {
            binding.locationSettingSpinner.setSelection(notificationList[2].toInt())
        }

        // map表示ボタンクリック時の処理
        binding.locationMapButton.setOnClickListener {
            val map = Intent(this, MapActivity::class.java)
            map.putExtra("position", position)
            startActivity(map)
        }

        val toolbar = binding.locationToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        if (notificationList[0] == "4") {
            binding.locationNameEdit.isVisible = true
            binding.locationNameEdit.setText(notificationList[1])
            binding.locationSettingSpinner.setSelection(locationNameList.lastIndex)
        }
    }

     // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                if (notificationList[0] == "4") {
                    notificationList[1] = binding.locationNameEdit.text.toString()
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