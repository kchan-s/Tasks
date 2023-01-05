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

    // ホーム画面のメモ部分の実装

    companion object {
        val titleData = listOf(
            "メモ1",
            "メモ2",
            "メモ3",
            "メモ4",
            "メモ5",
            "メモ6",
            "メモ7",
            "メモ8",
            "メモ9",
            "メモ10",
            "a",
            "b",
            "c",
            "d",
            "e"
        )
        val settingData = listOf(
            "",
            "",
            "2022/11/8 13:20 〜 2022/11/9 15:08",
            "",
            "",
            "高知工科大学",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
        val imageData = listOf(
            R.drawable.space,
            R.drawable.ic_baseline_lock_24,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space,
            R.drawable.space
        )

        var comp: Boolean = false // 完了・未完了
        var lock: Boolean = false // ロック・未ロック
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleText: TextView
        val settingText: TextView
        val lockImageView: ImageView
        val checkBox: CheckBox
        init {
            titleText = view.findViewById(R.id.delete_title_text)
            settingText = view.findViewById(R.id.delete_setting_text)
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
        viewHolder.settingText.text = item2

        val item3 = imageData[position]
        viewHolder.lockImageView.setImageResource(item3)

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                viewHolder.lockImageView.setOnClickListener {
                    if (lock) {
                        lock = false
                        viewHolder.lockImageView.setImageResource(R.drawable.space)
                    } else {
                        lock = true
                        viewHolder.lockImageView.setImageResource(R.drawable.ic_baseline_lock_24)
                    }
                }
                viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    comp = isChecked
                }

                v.setOnClickListener { view ->
                    val aPosition = viewHolder.adapterPosition
                    val context: Context = view.context
                    val intent = Intent(context, EditActivity::class.java)
                    intent.putExtra("title", titleData[aPosition])
                    // 現状はid代わり
                    intent.putExtra("position", aPosition)
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun getItemCount() = titleData.size
}