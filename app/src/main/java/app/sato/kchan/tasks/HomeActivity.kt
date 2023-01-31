package app.sato.kchan.tasks

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.sato.kchan.tasks.databinding.HomeActivityBinding
import app.sato.kchan.tasks.fanction.Account
import app.sato.kchan.tasks.fanction.Connect
import app.sato.kchan.tasks.fanction.ConnectionWrapper
import app.sato.kchan.tasks.fanction.DataOperator
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    companion object {
        lateinit var context: Context
    }
    private lateinit var binding: HomeActivityBinding
    val adapter = HomeMemoListAdapter()

    // 画面作成
    override fun onCreate(savedInstanceState: Bundle?) {
        context = applicationContext
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = HomeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //--- 初期化処理 ここから ------------------------------
//        println(">------")
//        val con = Connect()
//        con.setRequest("{\"type\":\"Hello\"}")
//        println("-->----")
//        while (!con.isEnd(10)){println("Wait")}
//        println("------>")

//        val account: Account = Account()
//        DataOperator().sync()
        //--- 初期化処理 ここまで ------------------------------

        val connectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectionManager.getNetworkCapabilities(connectionManager.activeNetwork)

        if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    val foregroundServiceIntent =
                        Intent(this, ForegroundNotificationService::class.java)
                    this.startForegroundService(foregroundServiceIntent)
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        val foregroundServiceIntent =
                            Intent(this, ForegroundNotificationService::class.java)
                        this.startForegroundService(foregroundServiceIntent)
                    }
                    Toast.makeText(this, "インターネットに接続していません", Toast.LENGTH_LONG).show()
                }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val foregroundServiceIntent =
                    Intent(this, ForegroundNotificationService::class.java)
                this.startForegroundService(foregroundServiceIntent)
            }
            Toast.makeText(this, "インターネットに接続していません", Toast.LENGTH_LONG).show()
        }

        // 以下リストの設定
        val recyclerView = binding.homeList
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (context.theme.resources.configuration.isNightModeActive) {
                binding.homeSettingButton.setBackgroundColor(Color.rgb(85,85, 85))
                binding.homeTrashButton.setBackgroundColor(Color.rgb(85, 85, 85))
            }
        }

        // 検索バーの設定
        binding.homeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return if (newText != "") {
                    HomeMemoListAdapter.searchNote.clear()
                    HomeMemoListAdapter().searchRequest(newText)
                    adapter.notifyDataSetChanged()
                    false
                } else {
                    HomeMemoListAdapter.search = false
                    adapter.notifyDataSetChanged()
                    false
                }
            }
            override fun onQueryTextSubmit(query: String): Boolean { return false }
        })

        // 設定ボタンタップ処理
        binding.homeSettingButton.setOnClickListener {
            settingButtonOnClick()
        }

        // 削除ボタンタップ処理
        binding.homeTrashButton.setOnClickListener {
            deleteButtonOnClick()
        }

        // 新規作成ボタンタップ処理
        binding.homeCreateMemoButton.setOnClickListener {
            newButtonOnClick()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.homeSearch.clearFocus()
        binding.homeSearch.setQuery("", false)
        adapter.notifyDataSetChanged()
    }

    //画面遷移　設定へ
    private fun settingButtonOnClick() {
        val settingIntent = Intent(this, SettingActivity::class.java)
        startActivity(settingIntent)
    }

    //画面遷移　削除へ
    private fun deleteButtonOnClick() {
        val deleteIntent = Intent(this, DeleteActivity::class.java)
        startActivity(deleteIntent)
    }

    //新規作成
    private fun newButtonOnClick() {
        EditActivity.new = true
        val newIntent = Intent(this, EditActivity::class.java)
        startActivity(newIntent)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_DayNight))
    }
}