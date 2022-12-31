package app.sato.kchan.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.sato.kchan.tasks.databinding.DeleteActivityBinding

class DeleteActivity: AppCompatActivity() {
    private lateinit var binding: DeleteActivityBinding

    // 画面作成とか(現状は触らなくていいです)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DeleteActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // オプションメニューの作成、表示
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    //画面遷移　ホームへ
    // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    //複数削除実行モジュール
//    private fun deleteButton_onClick() {
//        //while()?
//        NoteManager.selectByTempId()
//        NoteManager.isNote()
//        NoteManager.getNote()
//        NoteManager.delete()
//    }
}