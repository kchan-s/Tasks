package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.EditActivityBinding


class EditActivity : AppCompatActivity() {
    private lateinit var binding: EditActivityBinding
    var l = false // lock代わり
    var position = -1 // 初期値

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = EditActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        if (!HomeActivity.new) {
            // 呼び出された時にタイトルデータとメモのpositionを受け取る
            position = intent.getIntExtra("position", -1)
            val title = HomeMemoListAdapter.titleData[position]
            val detail = HomeMemoListAdapter.detailData[position]
            // タイトルと詳細をいれる
            binding.titleEdit.setText(title)
            binding.memoEdit.setText(detail)
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // オプションメニュー作成
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lock -> {
                //ロック押下
                l = !l
                //lockButton_onClick()
            }
            R.id.menu_delete -> {
                //削除押下
                //deleteButton_onClick()
                finish()
            }
            R.id.menu_time -> {
                //時間通知押下
                noticeButton_onClick()
            }
            R.id.menu_spot -> {
                //場所通知押下
                locationButton_onClick()
            }
            android.R.id.home -> {
                // 戻るボタン
                //backButton_onClick()
                if (HomeActivity.new && binding.titleEdit.text.toString() != "") {
                    HomeMemoListAdapter.titleData.add(binding.titleEdit.text.toString())
                    HomeMemoListAdapter.detailData.add(binding.memoEdit.text.toString())
                    HomeMemoListAdapter.lock.add(l)
                    HomeMemoListAdapter.comp.add(false) // 作成時点ではcheckはfalse
                    HomeActivity.new = false
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //ロック・未ロック
//    private fun lockButton_onClick() {
//        if(Note.isLock()){
//            Note.setUnlock()
//        }else{
//            Note.setLock()
//        }
//    }
//
//    //削除
//    private fun deleteButton_onClick() {
//        Note.delete()
//        画面遷移？
//    }

    //画面遷移　時間通知
    private fun noticeButton_onClick() {
        intent = Intent(this, TimeActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    //画面遷移　場所通知
    private fun locationButton_onClick() {
        intent = Intent(this, LocationActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}