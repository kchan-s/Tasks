package app.sato.kchan.tasks

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.sato.kchan.tasks.fanction.NoteManager
import org.w3c.dom.Text
import java.time.format.DateTimeFormatter

class DeleteMemoListAdapter: RecyclerView.Adapter<DeleteMemoListAdapter.ViewHolder>() {

    val noteManager = NoteManager()
    
    companion object {
        val selectedItem = mutableListOf<String>()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.delete_list_title_text)
        val noticeText: TextView = view.findViewById(R.id.delete_list_notice_text)
        val locationText: TextView = view.findViewById(R.id.delete_list_location_text)
        val memoList: LinearLayout = view.findViewById(R.id.delete_memo)
        val lockImage: ImageView = view.findViewById(R.id.delete_list_lock_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.delete_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        selectedItem.clear()
        noteManager.select(position)
        val note = noteManager.getNote()!!
        viewHolder.titleText.text = note.getTitle()

        val startTime = note.getNoticeShow()
        val stopTime = note.getNoticeHide()
        val location = note.getNoticeLocation()
        val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

        if (startTime != null && stopTime != null) viewHolder.noticeText.text = "${startTime.format(dateTimeFormat)} 〜 ${stopTime.format(dateTimeFormat)}"
        else if (startTime != null) viewHolder.noticeText.text = startTime.format(dateTimeFormat)

        if (location != null) viewHolder.locationText.text = location.getName()

        if (note.isLock()) viewHolder.lockImage.setImageResource(R.drawable.ic_baseline_lock_24)
        else viewHolder.lockImage.setImageResource(R.drawable.ic_baseline_lock_open_24)

        viewHolder.itemView.setOnClickListener { v ->
            val touchPosition = viewHolder.adapterPosition
            val deleteNoteManager = noteManager.copy()
            deleteNoteManager.select(touchPosition)

            v.setOnClickListener {
                if (!deleteNoteManager.getNote()!!.isLock()) {
                    if (isSelectedItem(deleteNoteManager.send())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (HomeActivity.context.theme.resources.configuration.isNightModeActive) {
                                viewHolder.memoList.setBackgroundColor(Color.rgb(85, 85, 85))
                            } else {
                                viewHolder.memoList.setBackgroundColor(Color.WHITE)
                            }
                        }
                        removeSelectedItem(deleteNoteManager.send())
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (HomeActivity.context.theme.resources.configuration.isNightModeActive) {
                                viewHolder.memoList.setBackgroundColor(Color.GRAY)
                            } else {
                                viewHolder.memoList.setBackgroundColor(Color.LTGRAY)
                            }
                        }
                        addSelectedItem(deleteNoteManager.send())
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        noteManager.search("")
        return noteManager.getNoteNumber()
    }

    //指定されたPositionのアイテムが選択済みか確認する
    private fun isSelectedItem(selected: String): Boolean = (selectedItem.contains(selected))

    private fun addSelectedItem(selected: String) {
        selectedItem.add(selected)
    }

    private fun removeSelectedItem(selected: String) {
        selectedItem.remove(selected)
    }
}