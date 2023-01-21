package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.sato.kchan.tasks.databinding.LocationStockActivityBinding

class LocationStockActivity: AppCompatActivity(){
    private lateinit var binding: LocationStockActivityBinding
    var adapter = LocationStockAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = LocationStockActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // よく行く場所一覧のリスト設定
        val recyclerView = binding.locationStockList
        adapter = LocationStockAdapter()
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        // LocationStockRegisterActivityへの遷移
        binding.locationStockRegisterButton.setOnClickListener {
            createButton_onClick()
        }

        val toolbar = binding.locationStockToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
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

    private fun createButton_onClick() {
        val intent = Intent(this, LocationStockRegisterActivity::class.java)
        startActivity(intent)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}