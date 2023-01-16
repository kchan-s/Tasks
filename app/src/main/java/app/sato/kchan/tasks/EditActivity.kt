package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.EditActivityBinding
import app.sato.kchan.tasks.fanction.NoteManager


class EditActivity : AppCompatActivity() {
    private lateinit var binding: EditActivityBinding
    val nm = NoteManager()
    var position = -1
    var l = false // 新規作成時に使用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = EditActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        position = intent.getIntExtra("position", -1)

        if (position != -1) {
            nm.selectByTempId(position.toString())
            val n = nm.getNote()
            binding.editTitleEdit.setText(n.getTitle())
            binding.editMemoEdit.setText(n.getContent())
        }

        val toolbar = binding.editToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // オプションメニュー作成
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lock -> {
                //ロック押下
//                if (position != -1) HomeMemoListAdapter.lockData[position] = !HomeMemoListAdapter.lockData[position]
                lockButton_onClick(position)
            }
            R.id.menu_delete -> {
                //削除押下
                deleteButton_onClick()
//                if (position != -1) {
//                    HomeMemoListAdapter.titleData.removeAt(position)
//                    HomeMemoListAdapter.detailData.removeAt(position)
//                    HomeMemoListAdapter.notificationSettingData.removeAt(position)
//                    HomeMemoListAdapter.lockData.removeAt(position)
//                    HomeMemoListAdapter.completeData.removeAt(position)
//                }

            }
            R.id.menu_time -> {
                //時間通知押下
                noticeButton_onClick()
            }
            R.id.menu_spot -> {
                //場所通知押下
                locationButton_onClick()
            }
            android.R.id.home -> {
                // 戻るボタン
                //backButton_onClick()
                nm.create()
                position = nm.getTempId().toInt()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //ロック・未ロック
    private fun lockButton_onClick(position: Int) {
        nm.selectByTempId(position.toString())
        val note = nm.getNote()
        if(note.isLock()){
            note.setUnlock()
        }else{
            note.setLock()
        }
    }

    //削除
    private fun deleteButton_onClick() {
        nm.selectByTempId(position.toString())
        val note = nm.getNote()
        note.delete()

        finish()
    }

    //画面遷移　時間通知
    private fun noticeButton_onClick() {
        intent = Intent(this, TimeActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    //画面遷移　場所通知
    private fun locationButton_onClick() {
        intent = Intent(this, LocationActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}