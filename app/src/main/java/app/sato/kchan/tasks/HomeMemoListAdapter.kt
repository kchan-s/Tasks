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
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class HomeMemoListAdapter: RecyclerView.Adapter<HomeMemoListAdapter.ViewHolder>() {

    // ホーム画面のメモ部分の実装

    companion object {
        val titleData = mutableListOf("メモ1", "メモ2", "メモ3", "メモ4", "メモ5", "メモ6", "メモ7", "メモ8", "メモ9", "メモ10", "a", "b", "c", "d", "e")
        val detailData = mutableListOf("1", "", "あ", "", "", "", "う", "", "", "か", "", "", "お", "く", "")
        val settingData = mutableListOf(
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
        var comp = mutableListOf(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false) // 完了・未完了
        var lock = mutableListOf(true, false, true, false, false, false, false, false, false, false, false, false, false, false, false)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleText: TextView
        val settingText: TextView
        val lockImageView: ImageView
        val checkBox: CheckBox
        init {
            titleText = view.findViewById(R.id.title_text)
            settingText = view.findViewById(R.id.setting_text)
            lockImageView = view.findViewById(R.id.lock_image)
            checkBox = view.findViewById(R.id.check_box)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.home_memo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = titleData[position]
        viewHolder.titleText.text = item

        val item2 = settingData[position]
        when {
            item2[0] == "1" -> viewHolder.settingText.text = item2[1] + " 〜"
            item2[0] == "2" -> viewHolder.settingText.text = item2[1] + " 〜 " + item2[2]
            item2[0] == "3" || item2[0] == "4" -> viewHolder.settingText.text = item2[1]
        }

        if (lock[position]) viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
        else viewHolder.lockImageView.setImageResource(R.drawable.space)
        if (comp[position]) viewHolder.checkBox.isChecked = true

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val aPosition = viewHolder.adapterPosition

                viewHolder.lockImageView.setOnClickListener {
                    if (lock[aPosition]) {
                        lock[aPosition] = false
                        viewHolder.lockImageView.setImageResource(R.drawable.space)
                    } else {
                        lock[aPosition] = true
                        viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
                    }
                }
                viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    comp[aPosition] = isChecked
                }

                v.setOnClickListener { view ->
                    val context: Context = view.context
                    val intent = Intent(context, EditActivity::class.java)
                    // 現状はid代わり
                    intent.putExtra("position", aPosition)
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun getItemCount() = titleData.size
}