package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
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
            }
            startActivity(intent)
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}