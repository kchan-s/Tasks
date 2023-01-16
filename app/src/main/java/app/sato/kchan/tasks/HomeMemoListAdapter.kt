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
//        val titleData = mutableListOf("メモ1", "メモ2", "メモ3", "メモ4", "メモ5", "メモ6", "メモ7", "メモ8", "メモ9", "メモ10", "a", "b", "c", "d", "e")
//        val titleData = mutableListOf<String>()
//        val detailData = mutableListOf<String>()
//        val notificationSettingData = mutableListOf(
//            mutableListOf("0"),
//            mutableListOf("0"),
//            mutableListOf("2", "2022/11/8 13:20", "2022/11/9 15:08"),
//            mutableListOf("0"),
//            mutableListOf("0"),
//            mutableListOf("3", "高知工科大学", "2", "33.620917", "133.719833"),
//            mutableListOf("0"),
//            mutableListOf("0"),
//            mutableListOf("0"),
//            mutableListOf("1", "2023/01/11 01:30"),
//            mutableListOf("0"),
//            mutableListOf("0"),
//            mutableListOf("0"),
//            mutableListOf("0"),
//            mutableListOf("0")
//        )
//        val completeData = mutableListOf(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false) // 完了・未完了
//        val lockData = mutableListOf(true, false, true, false, false, false, false, false, false, false, false, false, false, false, false)
//        var searchIndex = mutableListOf<Int>()
//        var search = false
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val context = view.context
        val titleText = view.findViewById<TextView>(R.id.home_list_title_text)
        val noticeText = view.findViewById<TextView>(R.id.home_list_notice_text)
        val locationText = view.findViewById<TextView>(R.id.home_list_location_text)
        val lockImageView = view.findViewById<ImageView>(R.id.home_list_lock_image)
        val checkBox = view.findViewById<CheckBox>(R.id.home_list_check_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.home_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        DataSet(viewHolder, position)

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val aPosition = viewHolder.adapterPosition

                viewHolder.lockImageView.setOnClickListener {
                    lockButton_onClick(viewHolder, aPosition)
//                    if (lockData[aPosition]) {
//                        lockData[aPosition] = false
//                    } else {
//                        lockData[aPosition] = true
//                    }
                }

                viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    completeButton_onClick(aPosition)
                }

                v.setOnClickListener {
                    val intent = Intent(viewHolder.context, EditActivity::class.java)
//                    if (searchIndex.size == 0) intent.putExtra("position", aPosition)
//                    else intent.putExtra("position", searchIndex[aPosition])
                    intent.putExtra("position", aPosition)
                    viewHolder.context.startActivity(intent)
                }
            }
        })
    }

    //完了・未完了切替モジュール
    private fun completeButton_onClick(position: Int) {
        nm.selectByTempId(position.toString())
        nm.isNote()
        var note = nm.getNote()
        if(note.isComplete()){
            note.setUncomplete()
        }else{
            note.setComplete()
        }
    }

    //ロック・未ロック切替モジュール
    private fun lockButton_onClick(viewHolder: ViewHolder, position: Int) {
        nm.selectByTempId(position.toString())
        nm.isNote()
        val note = nm.getNote()
        if(note.isLock()){
            note.setUnlock()
            viewHolder.lockImageView.setImageResource(R.drawable.space)
        }else{
            note.setLock()
            viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
        }
    }

    private fun DataSet(viewHolder: ViewHolder, position: Int) {
        nm.selectByTempId(position.toString())
        val n = nm.getNote()

        val item = n.getTitle()
        viewHolder.titleText.text = item

        val startTime = n.getNoticeShow()
        val stopTime = n.getNoticeHide()
        val location = n.getNoticeLocation()
        val f = DateTimeFormatter.ofPattern("yyyy/mm/dd hh:mm")

        if (startTime != null && stopTime != null) viewHolder.noticeText.text = "${startTime.format(f)} 〜 ${stopTime.format(f)}"
        else if (startTime != null) viewHolder.noticeText.text = startTime.format(f)

        if (location != null) viewHolder.locationText.text = location.toString()

//        when {
//            item2[0] == "0" -> viewHolder.settingText.text = ""
//            item2[0] == "1" -> {
//                viewHolder.settingText.text = item2[1]
//                setAlarm(viewHolder.context, position)
//            }
//            item2[0] == "2" -> {
//                viewHolder.settingText.text = item2[1] + " 〜 " + item2[2]
//                setAlarm(viewHolder.context, position)
//            }
//            item2[0] == "3" || item2[0] == "4" -> viewHolder.settingText.text = item2[1]
//        }

        if (n.isLock()) viewHolder.lockImageView.setImageResource(R.drawable.space)
        else viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
        if (!n.isComplete()) viewHolder.checkBox.isChecked = true
    }

    fun searchRequest(text:String) {
        nm.search(text)
//        search = true
//        for (i in 0..titleData.lastIndex) {
//            if (titleData[i].contains(text)) {
//                searchIndex.add(i)
//            }
//        }
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
        val am =
            context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
    }

    override fun getItemCount(): Int {
        return 0
//       if (search) return searchIndex.size
//       else return titleData.size
    }
}