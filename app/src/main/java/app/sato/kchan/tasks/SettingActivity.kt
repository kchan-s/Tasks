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
        val data = listOf("カスタマイズ", "アカウント","初期通知時間", "よく行く場所", "自動削除", "ヘルプ")

        // ListViewにデータをセットする
        val list = binding.listview
        list.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            data
        )

        // 遷移先の場合分け
        list.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent()
            when (position) {
                //画面遷移　カスタム
                // customButton_onClick
                0 -> {
                    intent.setClass(this, CustomActivity::class.java)
                    startActivity(intent)
                }

                //画面遷移　アカウント
                // accountButton_onClick
                1 -> {
                    intent.setClass(this, AccountActivity::class.java)
                    startActivity(intent)
                }

                //画面遷移　デフォ
                // defaultNoticeButton_onClick
                2 -> {
                    intent.setClass(this, DefaultNoticeActivity::class.java)
                    startActivity(intent)
                }

                //画面遷移　よくいく
                // locationButton_onClick
                3 -> {
                    intent.setClass(this, LocationStockActivity::class.java)
                    startActivity(intent)
                }

                //画面遷移　自動削除
                // autoDeletionButton_onClick
                4 -> {
                    intent.setClass(this, AutoDeletionActivity::class.java)
                    startActivity(intent)
                }

                //画面遷移　ヘルプ
                // helpButton_onClick
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

        // ツールバーの設定(触らなくていいです)
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

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}