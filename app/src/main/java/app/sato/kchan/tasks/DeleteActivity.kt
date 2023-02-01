package app.sato.kchan.tasks

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.sato.kchan.tasks.databinding.DeleteActivityBinding
import app.sato.kchan.tasks.fanction.NoteManager
import app.sato.kchan.tasks.fanction.NoticeManager

class DeleteActivity: AppCompatActivity() {
    private lateinit var binding: DeleteActivityBinding
    val noteManager = NoteManager()

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
                deleteButtonOnClick()
                finish()
            }
            android.R.id.home-> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //複数削除実行モジュール
    private fun deleteButtonOnClick() {
        for (i in 0 until DeleteMemoListAdapter.selectedItem.size) {
            noteManager.receive(DeleteMemoListAdapter.selectedItem[i])
            val note = noteManager.getNote()!!
            val noticeManager = NoticeManager()
            noticeManager.searchByNote(note)

            val targetIntent = Intent(HomeActivity.context, ForegroundNotificationService::class.java)
            HomeActivity.context.stopService(targetIntent)
            HomeActivity.context.startForegroundService(targetIntent)
            note.setNoticeShow(null)
            note.setNoticeHide(null)
            note.setNoticeLocation(null)
            noteManager.delete()
        }
        DeleteMemoListAdapter.selectedItem.clear()
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_DayNight))
    }
}