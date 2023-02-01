package app.sato.kchan.tasks

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.sato.kchan.tasks.HomeActivity.Companion.context
import app.sato.kchan.tasks.fanction.LocationManager
import app.sato.kchan.tasks.fanction.Note
import app.sato.kchan.tasks.fanction.NoteManager
import app.sato.kchan.tasks.fanction.NoticeManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class HomeMemoListAdapter: RecyclerView.Adapter<HomeMemoListAdapter.ViewHolder>() {

    val noteManager = NoteManager()

    companion object {
        var searchNote = mutableListOf<Note?>()
        var searchSendText = listOf<String>()
        var search = false
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context: Context = view.context
        val titleText: TextView = view.findViewById(R.id.home_list_title_text)
        val noticeText: TextView = view.findViewById(R.id.home_list_notice_text)
        val locationText: TextView = view.findViewById(R.id.home_list_location_text)
        val lockImageView: ImageView = view.findViewById(R.id.home_list_lock_image)
        val checkBox: ImageView = view.findViewById(R.id.home_list_check_image)
        val list: LinearLayout = view.findViewById(R.id.home_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.home_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val note = if (!search)  {
            noteManager.select(position)
            noteManager.getNote()!!
        } else {
            searchNote[position]!!
        }
        dataSet(viewHolder, note)

        viewHolder.itemView.setOnClickListener { v ->
            val adapterPosition = viewHolder.adapterPosition

            // リスト内の完了・未完了のチェックボックスタップ処理
            viewHolder.checkBox.setOnClickListener {
                completeButtonOnClick(viewHolder, position)
            }

            // リスト内のロック部分のタップ処理
            viewHolder.lockImageView.setOnClickListener {
                lockButtonOnClick(viewHolder, position)
            }

            v.setOnClickListener {
                taskmemoOnClick(viewHolder, adapterPosition)
            }
        }
    }

    //完了・未完了切替モジュール
    private fun completeButtonOnClick(viewHolder: ViewHolder, position: Int) {
        val completeNoteManager = noteManager.copy()
        completeNoteManager.select(position)
        val note = completeNoteManager.getNote()!!
        val noticeManager = NoticeManager()
        noticeManager.searchByNote(note)

        if (note.isComplete()) {
            note.setUncomplete()
            viewHolder.checkBox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (context.theme.resources.configuration.isNightModeActive) {
                    viewHolder.list.setBackgroundColor(Color.rgb(85, 85, 85))
                } else {
                    viewHolder.checkBox.setBackgroundColor(Color.WHITE)
                    viewHolder.list.setBackgroundColor(Color.WHITE)
                }
            }
        } else {
            viewHolder.checkBox.setImageResource(R.drawable.ic_baseline_check_circle_24)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (context.theme.resources.configuration.isNightModeActive) {
                    viewHolder.list.setBackgroundColor(Color.GRAY)
                } else {
                    viewHolder.checkBox.setBackgroundColor(Color.LTGRAY)
                    viewHolder.list.setBackgroundColor(Color.LTGRAY)
                }
            }

            val targetIntent = Intent(context, ForegroundNotificationService::class.java)
            context.stopService(targetIntent)
            context.startForegroundService(targetIntent)

            note.setComplete()
            viewHolder.noticeText.text = ""
            note.setNoticeShow(null)
            note.setNoticeHide(null)
            viewHolder.locationText.text = ""
            note.setNoticeLocation(null)
        }
    }

    //ロック・未ロック切替モジュール
    private fun lockButtonOnClick(viewHolder: ViewHolder, position: Int) {
        val lockNoteManager = noteManager.copy()
        lockNoteManager.select(position)
        val note = lockNoteManager.getNote()!!
        if (note.isLock()) {
            note.setUnlock()
            viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_open_24)
        } else {
            note.setLock()
            viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
        }
    }

    // タスクメモのクリック処理
    private fun taskmemoOnClick(viewHolder: ViewHolder, position: Int) {
        EditActivity.new = false
        val editIntent = Intent(viewHolder.context, EditActivity::class.java)
        val received = if (!search) {
            noteManager.select(position)
            noteManager.send()
        } else {
            searchSendText[position]
        }
        editIntent.putExtra("received", received)
        viewHolder.context.startActivity(editIntent)
    }

    // リストの初期設定
    private fun dataSet(viewHolder: ViewHolder, note: Note) {
        if (noteManager.isNote()) {
            viewHolder.titleText.setText(note.getTitle())
            val startTime = note.getNoticeShow()
            val stopTime = note.getNoticeHide()
            val location = note.getNoticeLocation()
            val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

            if (startTime != null && stopTime != null) {
                viewHolder.noticeText.text = "${startTime.format(dateTimeFormat)} 〜 ${stopTime.format(dateTimeFormat)}"
            } else if (startTime != null) {
                viewHolder.noticeText.text = startTime.format(dateTimeFormat)
            }
            else viewHolder.noticeText.text = ""

            if (location != null) {
                if (location.getName() != "") {
                    viewHolder.locationText.text = location.getName()
                } else if (location.getAddress() != "null") {
                    viewHolder.locationText.text = location.getAddress()
                }
            }
            else viewHolder.locationText.text = ""

            if (note.isLock()) viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
            else viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_open_24)
            if (note.isComplete()) {
                viewHolder.checkBox.setImageResource(R.drawable.ic_baseline_check_circle_24)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (context.theme.resources.configuration.isNightModeActive) {
                        viewHolder.list.setBackgroundColor(Color.GRAY)
                    } else {
                        viewHolder.checkBox.setBackgroundColor(Color.LTGRAY)
                        viewHolder.list.setBackgroundColor(Color.LTGRAY)
                    }
                }
            } else {
                viewHolder.checkBox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (context.theme.resources.configuration.isNightModeActive) {
                        viewHolder.list.setBackgroundColor(Color.rgb(85, 85, 85))
                    } else {
                        viewHolder.checkBox.setBackgroundColor(Color.WHITE)
                        viewHolder.list.setBackgroundColor(Color.WHITE)
                    }
                }
            }
        }
    }

    fun searchRequest(text: String) {
        search = true
        noteManager.search(text)
        val sendText = noteManager.send()
        searchSendText = sendText.split("|")
        for (i in 1..noteManager.getNoteNumber()) {
            searchNote.add(noteManager.getNote())
            noteManager.next()
        }
    }

    override fun getItemCount(): Int {
        return if (!search) {
            noteManager.search("")
            noteManager.getNoteNumber()
        } else {
            searchNote.size
        }
    }
}