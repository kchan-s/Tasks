package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.EditActivityBinding
import app.sato.kchan.tasks.fanction.NoteManager


class EditActivity : AppCompatActivity() {
    companion object {
        var new = false
    }
    private lateinit var binding: EditActivityBinding
    val nm = NoteManager()
    var note = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = EditActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        note = intent.getStringExtra("note").toString()

        if (!new) {
            nm.receive(note)
            val n = nm.getNote()
            binding.editTitleEdit.setText(n?.getTitle())
            binding.editMemoEdit.setText(n?.getContent())
        }

        val toolbar = binding.editToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // オプションメニュー作成
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!new) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.edit_menu, menu)
        }
        return true
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                //削除押下
                deleteButton_onClick()
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
                if (new && binding.editTitleEdit.text.toString() != "") {
                    val note = nm.create()
                    note.setTitle(binding.editTitleEdit.text.toString())
                    note.setContent(binding.editMemoEdit.text.toString())
                    note.setNoticeShow(null)
                    note.setNoticeHide(null)
                    note.setNoticeLocation(null)
                } else if (!new) {
                    nm.receive(note)
                    val note = nm.getNote()!!
                    note.setTitle(binding.editTitleEdit.text.toString())
                    note.setContent(binding.editMemoEdit.text.toString())
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //ロック・未ロック
    private fun lockButton_onClick(position: Int) {
        nm.selectByTempId(position.toString())
        val note = nm.getNote()
        if(note!!.isLock()){
            note.setUnlock()
        }else{
            note.setLock()
        }
    }

    //削除
    private fun deleteButton_onClick() {
        nm.receive(note)
        val note = nm.getNote()!!
        note.delete()
        finish()
    }

    //画面遷移　時間通知
    private fun noticeButton_onClick() {
        intent = Intent(this, TimeActivity::class.java)
        intent.putExtra("note", note)
        startActivity(intent)
    }

    //画面遷移　場所通知
    private fun locationButton_onClick() {
        intent = Intent(this, LocationActivity::class.java)
        intent.putExtra("note", note)
        startActivity(intent)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}