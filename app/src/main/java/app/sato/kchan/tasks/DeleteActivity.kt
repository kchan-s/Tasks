package app.sato.kchan.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.sato.kchan.tasks.databinding.DeleteActivityBinding

class DeleteActivity: AppCompatActivity() {
    private lateinit var binding: DeleteActivityBinding

    // 画面作成とか(現状は触らなくていいです)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = DeleteActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //RecyclerViewの取得
        val recyclerView = binding.memo

        //Adapterの設定
        val adapter = DeleteMemoListAdapter()
        recyclerView.adapter = adapter

        //LayoutManagerの設定
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // 境界線の設定
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // オプションメニューの作成、表示
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    //画面遷移　ホームへ
    // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_button -> {
                val deletePosition = DeleteMemoListAdapter.selectedItemPositions.sortedDescending()
                DeleteMemoListAdapter.selectedItemPositions.clear()
                for (i in deletePosition) {
                    HomeMemoListAdapter.titleData.removeAt(i)
                    HomeMemoListAdapter.detailData.removeAt(i)
                    HomeMemoListAdapter.settingData.removeAt(i)
                    HomeMemoListAdapter.lock.removeAt(i)
                    HomeMemoListAdapter.comp.removeAt(i)
                }
                // 削除処理
                finish()
            }
            android.R.id.home-> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    //複数削除実行モジュール
//    private fun deleteButton_onClick() {
//        //while()?
//        NoteManager.selectByTempId()
//        NoteManager.isNote()
//        NoteManager.getNote()
//        NoteManager.delete()
//    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}