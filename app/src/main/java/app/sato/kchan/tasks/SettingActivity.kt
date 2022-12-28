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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val data = listOf("カスタマイズ", "アカウント","初期通知時間", "よく行く場所", "自動削除", "ヘルプ")

        // ListViewにデータをセットする
        val list = binding.listview
        list.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            data
        )

        list.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent()
            when (position) {
                0 -> intent.setClass(this, CustomActivity::class.java)
                1 -> intent.setClass(this, AccountActivity::class.java)
                2 -> intent.setClass(this, DefaultNoticeActivity::class.java)
                3 -> intent.setClass(this, LocationStockActivity::class.java)
                4 -> intent.setClass(this, AutoDeletionActivity::class.java)
                0, 1, 2, 3, 4 -> startActivity(intent)
                5 -> {
                    // アプリ内にWebView埋め込み
                    // 戻るボタン機能させたいならintentで遷移？
                    val webView = WebView(this)
                    webView.webViewClient = WebViewClient()
                    webView.getSettings().setJavaScriptEnabled(true) // JavaScriptを有効にする
                    webView.loadUrl("https://google.com") // URLを読み込む
                    setContentView(webView)
                }
            }
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}