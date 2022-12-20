package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.sato.kchan.tasks.databinding.HomeActivityBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //RecyclerViewの取得
        val recyclerView = binding.memo

        //Adapterの設定
        val adapter = MemoListAdapter()
        recyclerView.adapter = adapter

        //LayoutManagerの設定
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // 境界線の設定
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        binding.homeSetting.setOnClickListener {
            val settingIntent = Intent(this, SettingActivity::class.java)
            startActivity(settingIntent)
        }

        binding.homeTrash.setOnClickListener {
            val deleteIntent = Intent(this, DeleteActivity::class.java)
            startActivity(deleteIntent)
        }

        binding.floatingActionButton.setOnClickListener {
            val newIntent = Intent(this, EditActivity::class.java)
            startActivity(newIntent)
        }
    }
}