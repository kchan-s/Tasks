package app.sato.kchan.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.sato.kchan.tasks.databinding.DeleteActivityBinding
import app.sato.kchan.tasks.fanction.NoteManager

class DeleteActivity: AppCompatActivity() {
    private lateinit var binding: DeleteActivityBinding
    val nm = NoteManager()

    // 画面作成とか(現状は触らなくていいです)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = DeleteActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // リストの設定
        val recyclerView = binding.deleteList
        val adapter = DeleteMemoListAdapter()
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        val toolbar = binding.deleteToolbar
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

    // 画面遷移の場合分け
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_button -> {
                deleteButton_onClick()
                finish()
            }
            android.R.id.home-> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //複数削除実行モジュール
    private fun deleteButton_onClick() {
        val deletePosition = DeleteMemoListAdapter.selectedItemPositions.sortedDescending()
        DeleteMemoListAdapter.selectedItemPositions.clear()
        for (i in deletePosition) nm.select(i)
        nm.deleteAll()
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}