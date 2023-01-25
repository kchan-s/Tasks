package app.sato.kchan.tasks

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.sato.kchan.tasks.fanction.Note
import app.sato.kchan.tasks.fanction.NoteManager
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class HomeMemoListAdapter: RecyclerView.Adapter<HomeMemoListAdapter.ViewHolder>() {

    private val requestCode = 1
    val nm = NoteManager()

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
        val checkBox: CheckBox = view.findViewById(R.id.home_list_check_box)
        val list: LinearLayout = view.findViewById(R.id.home_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.home_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val note: Note

        if (!search)  {
            nm.select(position)
            note = nm.getNote()!!
        } else {
            note = searchNote[position]!!
        }
        dataSet(viewHolder, note)

            viewHolder.itemView.setOnClickListener { v ->
                var adapterPosition = viewHolder.adapterPosition

                // リスト内の完了・未完了のチェックボックスタップ処理
                viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    completeButton_onClick(viewHolder, position, isChecked)
                }

                // リスト内のロック部分のタップ処理
                viewHolder.lockImageView.setOnClickListener {
                    lockButton_onClick(viewHolder, position)
                }

                v.setOnClickListener {
                    taskmemo_onClick(viewHolder, adapterPosition)
                }
            }
    }

    //完了・未完了切替モジュール
    private fun completeButton_onClick(viewHolder: ViewHolder, position: Int, isChecked: Boolean) {
        val completeNoteManager = NoteManager()
        completeNoteManager.receive(nm.send())
        val note = completeNoteManager.getNote()!!
        if (isChecked) {
            note.setComplete()
            viewHolder.list.setBackgroundColor(Color.LTGRAY)
        } else {
            note.setUncomplete()
            viewHolder.list.setBackgroundColor(Color.WHITE)
        }
    }

    //ロック・未ロック切替モジュール
    private fun lockButton_onClick(viewHolder: ViewHolder, position: Int) {
        var lockNoteManager = NoteManager()
        lockNoteManager.receive(nm.send())
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
    private fun taskmemo_onClick(viewHolder: ViewHolder, position: Int) {
        val note: String
        EditActivity.new = false
        val intent = Intent(viewHolder.context, EditActivity::class.java)
        if (!search) {
            nm.select(position)
            note = nm.send()
        } else {
            note = searchSendText[position]
        }
        intent.putExtra("note", note)
        viewHolder.context.startActivity(intent)
    }

    // リストの初期設定
    private fun dataSet(viewHolder: ViewHolder, note: Note) {
        if (nm.isNote()) {
            viewHolder.titleText.setText(note.getTitle())
            val startTime = note.getNoticeShow()
            val stopTime = note.getNoticeHide()
            val location = note.getNoticeLocation()
            val f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

            if (startTime != null && stopTime != null) {
                viewHolder.noticeText.text = "${startTime.format(f)} 〜 ${stopTime.format(f)}"

            }
            else if (startTime != null) {
                viewHolder.noticeText.text = startTime.format(f)
            }
            else viewHolder.noticeText.text = ""

            if (location != null) viewHolder.locationText.text = location.toString()
            else viewHolder.locationText.text = ""

            if (note.isLock()) viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
            else viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_open_24)
            if (note.isComplete()) {
                viewHolder.checkBox.isChecked = true
                viewHolder.list.setBackgroundColor(Color.LTGRAY)
            }
        }
    }

    fun searchRequest(text: String) {
        search = true
        nm.search(text)
        val sendText = nm.send()
        searchSendText = sendText.split("|")
        for (i in 1..nm.getNoteNumber()) {
            searchNote.add(nm.getNote())
            nm.next()
        }
    }

    fun setAlarm(context: Context, position: Int, note: Note) {
        val intent = Intent(context, AlarmNotification::class.java)
        val sdFormat = SimpleDateFormat("yyyy/mm/dd hh:mm")

        val start = sdFormat.parse(note.getNoticeShow().toString())
        val startCalendar = Calendar.getInstance()
        startCalendar.set(start.year, start.month, start.day, start.hours, start.minutes, 0)

        if (note.getNoticeHide() != null) {
            val stop = sdFormat.parse(note.getNoticeHide().toString())
            val stopCalendar = Calendar.getInstance()
            stopCalendar.set(stop.year, stop.month, stop.day, stop.hours, stop.minutes, 59)
            intent.putExtra("deleteTime", stopCalendar.timeInMillis - startCalendar.timeInMillis)
        }

        intent.putExtra("RequestCode", requestCode)
        intent.putExtra("position", position)

        val pending = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // アラームをセットする
        val am = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        am.setExact(
            AlarmManager.RTC_WAKEUP,
            startCalendar.getTimeInMillis(), pending
        )

        Toast.makeText(context, "set", Toast.LENGTH_LONG).show()
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmNotification::class.java)
        val pending = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // アラームを解除する
        val am = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
        }

    override fun getItemCount(): Int {
        if (!search) {
            nm.search("")
            return nm.getNoteNumber()
        } else {
            return searchNote.size
        }
    }
}