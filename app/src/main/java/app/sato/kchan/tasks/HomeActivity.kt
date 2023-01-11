package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.sato.kchan.tasks.databinding.HomeActivityBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeActivityBinding
    val adapter = HomeMemoListAdapter()

    companion object {
        var new = false
    }

    // 画面作成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = HomeActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //RecyclerViewの取得
        val recyclerView = binding.homeList

        //Adapterの設定
        recyclerView.adapter = adapter

        //LayoutManagerの設定
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // 境界線の設定
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        binding.homeSettingButton.setOnClickListener {
            settingButton_onClick()
        }

        binding.homeTrashButton.setOnClickListener {
            deleteButton_onClick()
        }
        binding.homeCreateMemoButton.setOnClickListener {
            newButton_onClick()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    //画面遷移　設定へ
    private fun settingButton_onClick() {
        val settingIntent = Intent(this, SettingActivity::class.java)
        startActivity(settingIntent)
    }

    //画面遷移　削除へ
    private fun deleteButton_onClick() {
        val deleteIntent = Intent(this, DeleteActivity::class.java)
        startActivity(deleteIntent)
    }

    //画面遷移　編集へ
//    private fun （？）_onClick() {
//        val newIntent = Intent(this, EditActivity::class.java)
//        startActivity(newIntent)
//    }

    //新規作成
    private fun newButton_onClick() {
        new = true
        val newIntent = Intent(this, EditActivity::class.java)
        startActivity(newIntent)
    }

//    //リスト更新モジュール
//    private fun searchBox_onEditorAction() {
//        listUpdate()
//    }
//
//    //完了・未完了切替モジュール
//    private fun completeButton_onClick() {
//        NoteManager.selectByTempId()
//        NoteManager.isNote()
//        NoteManager.getNote()
//        if(Note.isComplete()){
//            Note.setUnComplete()
//        }else{
//            Note.setComplete()
//        }
//    }
//
//    //ロック・未ロック切替モジュール
//    private fun lockButton_onClick() {
//        NoteManager.selectByTempId()
//        NoteManager.isNote()
//        NoteManager.getNote()
//        if(Note.isLock()){
//            Note.setUnlock()
//        }else{
//            Note.setLock()
//        }
//    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}