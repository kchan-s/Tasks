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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class HomeMemoListAdapter: RecyclerView.Adapter<HomeMemoListAdapter.ViewHolder>() {

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
        val checkBox: ImageView = view.findViewById(R.id.home_list_check_image)
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
                viewHolder.checkBox.setOnClickListener {
                    completeButton_onClick(viewHolder, position)
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
    private fun completeButton_onClick(viewHolder: ViewHolder, position: Int) {
        val completeNoteManager = nm.copy()
        completeNoteManager.select(position)
        val note = completeNoteManager.getNote()!!
        if (note.isComplete()) {
            note.setUncomplete()
            viewHolder.checkBox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            viewHolder.checkBox.setBackgroundColor(Color.WHITE)
            viewHolder.list.setBackgroundColor(Color.WHITE)
        } else {
            note.setComplete()
            viewHolder.checkBox.setImageResource(R.drawable.ic_baseline_check_circle_24)
            viewHolder.checkBox.setBackgroundColor(Color.LTGRAY)
            viewHolder.list.setBackgroundColor(Color.LTGRAY)
        }
    }

    //ロック・未ロック切替モジュール
    private fun lockButton_onClick(viewHolder: ViewHolder, position: Int) {
        val lockNoteManager = nm.copy()
        lockNoteManager.select(position)
        val note = lockNoteManager.getNote()!!
        if (note.isLock()) {
            println(note.getTitle())
            println(note.isLock())
            note.setUnlock()
            viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_open_24)
        } else {
            println(note.getTitle())
            println(note.isLock())
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
            println(location)
            val f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

            if (startTime != null && stopTime != null) {
                viewHolder.noticeText.text = "${startTime.format(f)} 〜 ${stopTime.format(f)}"
                setAlarm(viewHolder.context, note)
            } else if (startTime != null) {
                viewHolder.noticeText.text = startTime.format(f)
                setAlarm(viewHolder.context, note)
            }
            else viewHolder.noticeText.text = ""

            if (location != null) viewHolder.locationText.text = location.toString()
            else viewHolder.locationText.text = ""

            if (note.isLock()) viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
            else viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_open_24)
            if (note.isComplete()) {
                viewHolder.checkBox.setImageResource(R.drawable.ic_baseline_check_circle_24)
                viewHolder.checkBox.setBackgroundColor(Color.LTGRAY)
                viewHolder.list.setBackgroundColor(Color.LTGRAY)
            } else {
                viewHolder.checkBox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
                viewHolder.checkBox.setBackgroundColor(Color.WHITE)
                viewHolder.list.setBackgroundColor(Color.WHITE)
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

    fun setAlarm(context: Context, note: Note) {
        val intent = Intent(context, AlarmNotification::class.java)

        val start = note.getNoticeShow()!!
        val startCalendar = Calendar.getInstance()
        val stop = note.getNoticeHide()
        val stopCalendar = Calendar.getInstance()

        if ((start.isAfter(LocalDateTime.now()) || start.isEqual(LocalDateTime.now())) && stop != null) {
            startCalendar.set(start.year, start.monthValue-1, start.dayOfMonth, start.hour, start.minute, 0)
            stopCalendar.set(stop.year, stop.monthValue-1, stop.dayOfMonth, stop.hour, stop.minute, 0)
            intent.putExtra("deleteTime", stopCalendar.timeInMillis - startCalendar.timeInMillis)
        } else if (start.isAfter(LocalDateTime.now()) || start.isEqual(LocalDateTime.now())) {
            startCalendar.set(start.year, start.monthValue-1, start.dayOfMonth, start.hour, start.minute, 0)
        } else if (stop == null) {
            return
        } else if (start.isBefore(LocalDateTime.now()) && (stop.isAfter(LocalDateTime.now()) || stop.isEqual(LocalDateTime.now()))) {
            val now = LocalDateTime.now()
            startCalendar.set(now.year, now.monthValue-1, now.dayOfMonth, now.hour, now.minute, now.second)
            stopCalendar.set(stop.year, stop.monthValue-1, stop.dayOfMonth, stop.hour, stop.minute, 0)
            intent.putExtra("deleteTime", stopCalendar.timeInMillis - startCalendar.timeInMillis)
        } else {
            return
        }

        val uuid = UUID.randomUUID().hashCode()
        intent.putExtra("id", uuid)
        intent.putExtra("title", note.getTitle())
        intent.putExtra("content", note.getContent())

        val pending = PendingIntent.getBroadcast(
            context, uuid, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // アラームをセットする
        println(startCalendar)
        val am = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        am.setExact(
            AlarmManager.RTC_WAKEUP,
            startCalendar.getTimeInMillis(), pending
        )
        println(uuid)
    }

//    fun cancelAlarm(context: Context) {
//        val intent = Intent(context, AlarmNotification::class.java)
//        val pending = PendingIntent.getBroadcast(
//            context, uuid, intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // アラームを解除する
//        val am = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
//        am.cancel(pending)
//        }

    override fun getItemCount(): Int {
        if (!search) {
            nm.search("")
            return nm.getNoteNumber()
        } else {
            return searchNote.size
        }
    }
}