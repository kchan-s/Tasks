package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.SettingActivityBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: SettingActivityBinding

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = SettingActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }
        val data = listOf("アカウント", "よく行く場所", "ヘルプ")

        // ListViewにデータをセットする
        val list = binding.settingList
        list.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            data
        )

        // 遷移先の場合分け
        list.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> accountButtonOnClick()
                1 -> locationButtonOnClick()
                2 -> helpButtonOnClick()
            }
        }

        val toolbar = binding.settingToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val homeIntent = Intent(this, HomeActivity::class.java)
                startActivity(homeIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


//    // カスタマイズ画面に遷移
//    private fun customButton_onClick() {
//
//        fun hello():String{
//            var data = MyData()
//            data.setString("type", "Hello")
//            data.move("content")
//            val res: String = data.outJSON() ?: throw Exception("")
//            println(res)
//            return res
//        }
//
//
//        ConnectionWrapper.scope.launch{
//            ConnectionWrapper().executeServerConnection(hello())
//            Log.d("SettingActivity",ConnectionWrapper().postOutput())
//
//        }
//    }

    // アカウント画面に遷移
    private fun accountButtonOnClick() {
        intent.setClass(this, AccountActivity::class.java)
        startActivity(intent)
    }

//    // 標準通知時間設定画面に遷移
//    private fun defaultNoticeButton_onClick() {
//        intent.setClass(this, DefaultNoticeActivity::class.java)
//        startActivity(intent)
//    }

    // よく行く場所一覧画面に遷移
    private fun locationButtonOnClick() {
        intent.setClass(this, LocationStockActivity::class.java)
        startActivity(intent)
    }

//    // 自動削除設定画面に遷移
//    private fun autoDeletionButton_onClick() {
//        intent.setClass(this, AutoDeletionActivity::class.java)
//        startActivity(intent)
//    }

    // ヘルプページに遷移
    private fun helpButtonOnClick() {
        val webView = WebView(this)
        webView.webViewClient = WebViewClient()
        webView.getSettings().setJavaScriptEnabled(true) // JavaScriptを有効にする
        webView.loadUrl("https://google.com") // URLを読み込む
        setContentView(webView)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_DayNight))
    }
}