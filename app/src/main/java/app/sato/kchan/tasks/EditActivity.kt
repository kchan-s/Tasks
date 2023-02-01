package app.sato.kchan.tasks

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
    val noteManager = NoteManager()
    var received = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = EditActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        received = intent.getStringExtra("received").toString()

        if (!new) {
            noteManager.receive(received)
            val receivedNote = noteManager.getNote()!!
            binding.editTitleEdit.setText(receivedNote.getTitle())
            binding.editMemoEdit.setText(receivedNote.getContent())
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
                deleteButtonOnClick()
            }
            R.id.menu_time -> {
                //時間通知押下
                noticeButtonOnClick()
            }
            R.id.menu_spot -> {
                //場所通知押下
                locationButtonOnClick()
            }
            android.R.id.home -> {
                if (new && binding.editTitleEdit.text.toString() != "") {
                    val newNote = noteManager.create()
                    newNote.setTitle(binding.editTitleEdit.text.toString())
                    newNote.setContent(binding.editMemoEdit.text.toString())
                    newNote.setNoticeShow(null)
                    newNote.setNoticeHide(null)
                    newNote.setNoticeLocation(null)
                } else if (!new) {
                    val note = noteManager.getNote()!!
                    note.setTitle(binding.editTitleEdit.text.toString())
                    note.setContent(binding.editMemoEdit.text.toString())
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //削除
    private fun deleteButtonOnClick() {
        val note = noteManager.getNote()!!
        val sharedPreferences = getSharedPreferences("app_notification_id", MODE_PRIVATE)
        val cancelUuid = sharedPreferences.getInt(noteManager.send(), -1)
        if (cancelUuid != -1) {
            ForegroundNotificationService().cancelAlarm(applicationContext, cancelUuid)
            val notificationManager =
                HomeActivity.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.getNotificationChannel("notice")
            notificationManager.cancel(cancelUuid)
        }
        note.setNoticeLocation(null)
        note.delete()
        finish()
    }

    //画面遷移　時間通知
    private fun noticeButtonOnClick() {
        intent = Intent(this, TimeActivity::class.java)
        intent.putExtra("received", received)
        startActivity(intent)
    }

    //画面遷移　場所通知
    private fun locationButtonOnClick() {
        intent = Intent(this, LocationActivity::class.java)
        intent.putExtra("received", received)
        startActivity(intent)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_DayNight))
    }
}