package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.EditActivityBinding


class EditActivity : AppCompatActivity() {
    private lateinit var binding: EditActivityBinding

    //?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

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
                //lockButton_onClick()
                return true
            }
            R.id.menu_delete -> {
                //削除押下
                //deleteButton_onClick()
                return true
            }
            R.id.menu_time -> {
                //時間通知押下
                noticeButton_onClick()
                return true
            }
            R.id.menu_spot -> {
                //場所通知押下
                locationButton_onClick()
                return true
            }
            android.R.id.home -> {
                // 戻るボタン
                //backButton_onClick()
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
        startActivity(intent)
    }

    //画面遷移　場所通知
    private fun locationButton_onClick() {
        intent = Intent(this, LocationActivity::class.java)
        startActivity(intent)
    }
}