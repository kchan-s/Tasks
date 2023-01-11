package app.sato.kchan.tasks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeleteMemoListAdapter: RecyclerView.Adapter<DeleteMemoListAdapter.ViewHolder>() {

    val titleData = HomeMemoListAdapter.titleData
    val notificationSettingData = HomeMemoListAdapter.notificationSettingData
    val lockData = HomeMemoListAdapter.lockData
    
    companion object {
        val selectedItemPositions = mutableSetOf<Int>()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleText: TextView
        val settingText: TextView
        val memo: LinearLayout
        val lockImage: ImageView
        init {
            titleText = view.findViewById(R.id.delete_list_title_text)
            settingText = view.findViewById(R.id.delete_list_setting_text)
            memo = view.findViewById(R.id.delete_memo)
            lockImage = view.findViewById(R.id.delete_list_lock_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.delete_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.titleText.text = titleData[position]
        val notification = notificationSettingData[position]
        
        when {
            notification[0] == "1" -> viewHolder.settingText.text = notification[1] + "〜"
            notification[0] == "2" -> viewHolder.settingText.text = notification[1] + "〜" + notification[2]
            notification[0] == "3" -> viewHolder.settingText.text = notification[1]
        }

        if (lockData[position]) viewHolder.lockImage.setImageResource(R.drawable.ic_baseline_lock_24)
        else viewHolder.lockImage.setImageResource(R.drawable.space)

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                val touchPosition = viewHolder.adapterPosition
                v.setOnClickListener {
                    if (!lockData[touchPosition]) {
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

    override fun getItemCount() = HomeMemoListAdapter.titleData.size

    //指定されたPositionのアイテムが選択済みか確認する
    private fun isSelectedItem(position: Int): Boolean = (selectedItemPositions.contains(position))

    private fun addSelectedItem(position: Int) {
        selectedItemPositions.add(position)
    }

    private fun removeSelectedItem(position: Int) {
        selectedItemPositions.remove(position)
    }
}