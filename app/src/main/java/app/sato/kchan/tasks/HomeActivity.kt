package app.sato.kchan.tasks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.sato.kchan.tasks.databinding.HomeActivityBinding
import app.sato.kchan.tasks.fanction.Account
import app.sato.kchan.tasks.fanction.DataOperator

class HomeActivity : AppCompatActivity() {
    companion object {
        lateinit var context: Context
    }
    private lateinit var binding: HomeActivityBinding
    val adapter = HomeMemoListAdapter()

    init {
        val account: Account = Account()
//        DataOperator().sync()
    }

    // 画面作成
    override fun onCreate(savedInstanceState: Bundle?) {
        context = applicationContext
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = HomeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val foregroundServiceIntent = Intent(this, ForegroundNotificationService::class.java)
        this.startForegroundService(foregroundServiceIntent)

        // 以下リストの設定
        val recyclerView = binding.homeList
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        // 検索バーの設定
        binding.homeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText != "") {
                    HomeMemoListAdapter.searchNote.clear()
                    HomeMemoListAdapter().searchRequest(newText)
                    adapter.notifyDataSetChanged()
                    return false
                } else {
                    HomeMemoListAdapter.search = false
                    adapter.notifyDataSetChanged()
                    return false
                }
            }
            override fun onQueryTextSubmit(query: String): Boolean { return false }
//          listUpdate()
        })

        // 設定ボタンタップ処理
        binding.homeSettingButton.setOnClickListener {
            settingButton_onClick()
        }

        // 削除ボタンタップ処理
        binding.homeTrashButton.setOnClickListener {
            deleteButton_onClick()
        }

        // 新規作成ボタンタップ処理
        binding.homeCreateMemoButton.setOnClickListener {
            newButton_onClick()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.homeSearch.clearFocus()
        binding.homeSearch.setQuery("", false)
        adapter.notifyDataSetChanged()
    }

    //画面遷移　設定へ
    private fun settingButton_onClick() {
        val settingIntent = Intent(this, SettingActivity::class.java)
        startActivity(settingIntent)
    }

    //画面遷移　削除へ
    private fun deleteButton_onClick() {
        val deleteIntent = Intent(this, DeleteActivity::class.java)
        startActivity(deleteIntent)
    }

    //新規作成
    private fun newButton_onClick() {
        EditActivity.new = true
        val newIntent = Intent(this, EditActivity::class.java)
        startActivity(newIntent)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}