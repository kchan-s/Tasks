package app.sato.kchan.tasks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.sato.kchan.tasks.fanction.NoteManager
import java.time.format.DateTimeFormatter

class DeleteMemoListAdapter: RecyclerView.Adapter<DeleteMemoListAdapter.ViewHolder>() {

    val nm = NoteManager()
    
    companion object {
        val selectedItemPositions = mutableSetOf<Int>()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.home_list_title_text)
        val noticeText: TextView = view.findViewById(R.id.home_list_notice_text)
        val locationText: TextView = view.findViewById(R.id.home_list_location_text)
        val memo: LinearLayout = view.findViewById(R.id.delete_memo)
        val lockImage: ImageView = view.findViewById(R.id.delete_list_lock_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.delete_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        nm.selectByTempId(position.toString())
        val n = nm.getNote()
        viewHolder.titleText.text = n.getTitle()

        val startTime = n.getNoticeShow()
        val stopTime = n.getNoticeHide()
        val location = n.getNoticeLocation()
        val f = DateTimeFormatter.ofPattern("yyyy/mm/dd hh:mm")

        if (startTime != null && stopTime != null) viewHolder.noticeText.text = "${startTime.format(f)} 〜 ${stopTime.format(f)}"
        else if (startTime != null) viewHolder.noticeText.text = startTime.format(f)

        if (location != null) viewHolder.locationText.text = location.toString()

        if (n.isLock()) viewHolder.lockImage.setImageResource(R.drawable.ic_baseline_lock_24)
        else viewHolder.lockImage.setImageResource(R.drawable.space)

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                val touchPosition = viewHolder.adapterPosition
                nm.selectByTempId(touchPosition.toString())

                v.setOnClickListener {
                    if (!nm.getNote().isLock()) {
                        if (isSelectedItem(touchPosition)) {
                            viewHolder.memo.setBackgroundColor(Color.WHITE)
                            removeSelectedItem(touchPosition)
                        } else {
                            viewHolder.memo.setBackgroundColor(Color.LTGRAY)
                            addSelectedItem(touchPosition)
                        }
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int {
        nm.search("")
        return nm.getNoteNumber()
    }

    //指定されたPositionのアイテムが選択済みか確認する
    private fun isSelectedItem(position: Int): Boolean = (selectedItemPositions.contains(position))

    private fun addSelectedItem(position: Int) {
        selectedItemPositions.add(position)
    }

    private fun removeSelectedItem(position: Int) {
        selectedItemPositions.remove(position)
    }
}