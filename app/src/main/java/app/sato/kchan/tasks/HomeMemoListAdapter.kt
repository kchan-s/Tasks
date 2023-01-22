package app.sato.kchan.tasks

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.sato.kchan.tasks.fanction.NoteManager
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class HomeMemoListAdapter: RecyclerView.Adapter<HomeMemoListAdapter.ViewHolder>() {

    private val requestCode = 1
    val nm = NoteManager()

    companion object {
        var searchIndex = mutableListOf<Int>()
        var search = false
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context: Context = view.context
        val titleText: TextView = view.findViewById(R.id.home_list_title_text)
        val noticeText: TextView = view.findViewById(R.id.home_list_notice_text)
        val locationText: TextView = view.findViewById(R.id.home_list_location_text)
        val lockImageView: ImageView = view.findViewById(R.id.home_list_lock_image)
        val checkBox: CheckBox = view.findViewById(R.id.home_list_check_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.home_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (!search) dataSet(viewHolder, position)
        else dataSet(viewHolder, searchIndex[position])

            viewHolder.itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    var adapterPosition = viewHolder.adapterPosition
                    if (search) adapterPosition = searchIndex[adapterPosition]

                    // ホーム画面に存在するリスト内のロック部分のタップ処理
                    viewHolder.lockImageView.setOnClickListener {
                        lockButton_onClick(viewHolder, adapterPosition)
                    }

                    // ホーム画面に存在するリスト内の完了・未完了のチェックボックスタップ処理
                    viewHolder.checkBox.setOnCheckedChangeListener { _, _ ->
                        completeButton_onClick(adapterPosition)
                    }

                    v.setOnClickListener {
                        taskmemo_onClick(viewHolder, adapterPosition)
                    }
                }
            })
    }

    //完了・未完了切替モジュール
    private fun completeButton_onClick(position: Int) {
        nm.selectByTempId(position.toString())
        nm.isNote()
        val note = nm.getNote()
        if (note.isComplete()) {
            note.setUncomplete()
        } else {
            note.setComplete()
        }
    }

    //ロック・未ロック切替モジュール
    private fun lockButton_onClick(viewHolder: ViewHolder, position: Int) {
        nm.selectByTempId(position.toString())
        nm.isNote()
        val note = nm.getNote()
        if (note.isLock()) {
            note.setUnlock()
            viewHolder.lockImageView.setImageResource(R.drawable.space)
        } else {
            note.setLock()
            viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
        }
    }

    // タスクメモのクリック処理
    private fun taskmemo_onClick(viewHolder: ViewHolder, position: Int) {
        val intent = Intent(viewHolder.context, EditActivity::class.java)
//                    if (searchIndex.size == 0) intent.putExtra("position", adapterPosition)
//                    else intent.putExtra("position", searchIndex[adapterPosition])
        intent.putExtra("position", position)
        viewHolder.context.startActivity(intent)
    }

    // リストの設定
    private fun dataSet(viewHolder: ViewHolder, position: Int) {
        nm.selectByTempId(position.toString())
        val note = nm.getNote()
        viewHolder.titleText.setText(note.getTitle())
        println(note.getTitle())
        val startTime = note.getNoticeShow()
        val stopTime = note.getNoticeHide()
//        val location = n.getNoticeLocation()
        val f = DateTimeFormatter.ofPattern("yyyy/mm/dd hh:mm")

        if (startTime != null && stopTime != null) viewHolder.noticeText.text =
            "${startTime.format(f)} 〜 ${stopTime.format(f)}"
        else if (startTime != null) viewHolder.noticeText.text = startTime.format(f)

//        if (location != null) viewHolder.locationText.text = location.toString()

//        if (n.isLock()) viewHolder.lockImageView.setImageResource(R.drawable.space)
//        else viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
//        if (!n.isComplete()) viewHolder.checkBox.isChecked = true
    }

    fun searchRequest(text: String) {
        search = true
        for (i in 0..nm.getNoteNumber()) {
            nm.search(text)
            if (nm.isNote()) {
                searchIndex.add(nm.getNoteNumber())
                //        for (i in 0..titleData.lastIndex) {
//            if (titleData[i].contains(text)) {
//                searchIndex.add(i)
//            }
//        }}
            }
//        search = true
        }
    }

//    fun setAlarm(context: Context, position: Int) {
//        val intent = Intent(context, AlarmNotification::class.java)
//        val sdFormat = SimpleDateFormat("yyyy/mm/dd hh:mm")
//
//        val startDate = sdFormat.parse(notificationSettingData[position][1])
//        val startCalendar = Calendar.getInstance()
//        startCalendar.set(startDate.year, startDate.month, startDate.day, startDate.hours, startDate.minutes, 0)
//
//        if (notificationSettingData[position].size != 2) {
//            val stopDate = sdFormat.parse(notificationSettingData[position][2])
//            val stopCalendar = Calendar.getInstance()
//            stopCalendar.set(stopDate.year, stopDate.month, stopDate.day, stopDate.hours, stopDate.minutes, 59)
//            intent.putExtra("deleteTime", stopCalendar.timeInMillis - startCalendar.timeInMillis)
//        }
//
//        intent.putExtra("RequestCode", requestCode)
//        intent.putExtra("position", position)
//
//        val pending = PendingIntent.getBroadcast(
//            context, requestCode, intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // アラームをセットする
//        val am = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
//        am.setExact(
//            AlarmManager.RTC_WAKEUP,
//            startCalendar.getTimeInMillis(), pending
//        )
//
//        Toast.makeText(context, "set", Toast.LENGTH_LONG).show()
//    }

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
//            return 1
        } else {
            return searchIndex.size
        }
    }
}