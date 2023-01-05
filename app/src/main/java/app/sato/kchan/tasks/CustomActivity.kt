package app.sato.kchan.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import app.sato.kchan.tasks.databinding.CustomActivityBinding


class CustomActivity: AppCompatActivity(){
    private lateinit var binding: CustomActivityBinding


    // 画面作成とか(現状は触らなくていいです)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        val cEditor = cPreferences.edit()
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
        binding = CustomActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        binding.darkSwitch.setOnCheckedChangeListener { _, isChanged ->
            // データベースから現状の値を読み込んで入れる必要あり
            if (isChanged) {
                // ダークモード
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                // ノーマル
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
        }

        binding.pinkButton.setOnClickListener {
            cEditor?.putInt("theme", R.style.Theme_TaSks_Pink)
            cEditor.commit()
            finish()
            startActivity(Intent(this, CustomActivity::class.java))
            overridePendingTransition(0, 0)
        }

        binding.turquoiseButton.setOnClickListener {
            cEditor?.putInt("theme", R.style.Theme_TaSks_Turquoise)
            cEditor.commit()
            finish()
            startActivity(Intent(this, CustomActivity::class.java))
            overridePendingTransition(0, 0)
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    //ダークモード切り替え
//    private fun themeToggleSwitch_onClick() {
//    if(Timeの// トグルがオンになっているかどうか判定){
//            Info.setNormalTheme()
//        }else{
//            Info.setDarkTheme()
//        }
//    }

//    //カラー選択
//    private fun colorButton_onClick() {
//    //ボタンによって分岐？
//    }
}