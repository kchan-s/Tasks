package app.sato.kchan.tasks

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class HomeMemoListAdapter: RecyclerView.Adapter<HomeMemoListAdapter.ViewHolder>() {

    companion object {
        val titleData = mutableListOf("メモ1", "メモ2", "メモ3", "メモ4", "メモ5", "メモ6", "メモ7", "メモ8", "メモ9", "メモ10", "a", "b", "c", "d", "e")
        val detailData = mutableListOf("1", "", "あ", "", "", "", "う", "", "", "か", "", "", "お", "く", "")
        val notificationSettingData = mutableListOf(
            mutableListOf("0"),
            mutableListOf("0"),
            mutableListOf("2", "2022/11/8 13:20", "2022/11/9 15:08"),
            mutableListOf("0"),
            mutableListOf("0"),
            mutableListOf("3", "高知工科大学", "2", "33.620917", "133.719833"),
            mutableListOf("0"),
            mutableListOf("0"),
            mutableListOf("0"),
            mutableListOf("1", "2023/01/11 01:30"),
            mutableListOf("0"),
            mutableListOf("0"),
            mutableListOf("0"),
            mutableListOf("0"),
            mutableListOf("0")
        )
        val completeData = mutableListOf(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false) // 完了・未完了
        val lockData = mutableListOf(true, false, true, false, false, false, false, false, false, false, false, false, false, false, false)
        var searchIndex = mutableListOf<Int>()
        var search = false
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleText = view.findViewById<TextView>(R.id.home_list_title_text)
        val settingText = view.findViewById<TextView>(R.id.home_list_notification_text)
        val lockImageView = view.findViewById<ImageView>(R.id.home_list_lock_image)
        val checkBox = view.findViewById<CheckBox>(R.id.home_list_check_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.home_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (search) {
            DataSet(viewHolder, searchIndex[position])
            search = false
        } else DataSet(viewHolder, position)

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val aPosition = viewHolder.adapterPosition

                viewHolder.lockImageView.setOnClickListener {
                    if (lockData[aPosition]) {
                        lockData[aPosition] = false
                        viewHolder.lockImageView.setImageResource(R.drawable.space)
                    } else {
                        lockData[aPosition] = true
                        viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
                    }
                }
                viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    completeData[aPosition] = isChecked
                }

                v.setOnClickListener { view ->
                    val context: Context = view.context
                    val intent = Intent(context, EditActivity::class.java)
                    // 現状はid代わり
                    if (searchIndex.size == 0) intent.putExtra("position", aPosition)
                    else intent.putExtra("position", searchIndex[aPosition])
                    context.startActivity(intent)
                }
            }
        })
    }

    private fun DataSet(viewHolder: ViewHolder, position: Int) {

        val item = titleData[position]
        viewHolder.titleText.text = item

        val item2 = notificationSettingData[position]
        when {
            item2[0] == "0" -> viewHolder.settingText.text = ""
            item2[0] == "1" -> viewHolder.settingText.text = item2[1] + " 〜"
            item2[0] == "2" -> viewHolder.settingText.text = item2[1] + " 〜 " + item2[2]
            item2[0] == "3" || item2[0] == "4" -> viewHolder.settingText.text = item2[1]
        }

        if (lockData[position]) viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
        else viewHolder.lockImageView.setImageResource(R.drawable.space)
        if (completeData[position]) viewHolder.checkBox.isChecked = true
    }

    fun searchRequest(text:String) {
        search = true
        for (i in 0..titleData.lastIndex) {
            if (titleData[i].contains(text)) {
                searchIndex.add(i)
            }
        }
    }

    override fun getItemCount(): Int {
       if (search) return searchIndex.size
       else return titleData.size
    }
}