package app.sato.kchan.tasks

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.sato.kchan.tasks.databinding.AutoDeletionActivityBinding

class AutoDeletionActivity: AppCompatActivity(){
    private lateinit var binding: AutoDeletionActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = AutoDeletionActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // トグルの値読み込みが必要
        binding.autoDeletionSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 変更を保存
                binding.timingText.isVisible = true
                binding.timingSettingSpinner.isVisible = true
            } else {
                binding.timingText.isVisible = false
                binding.timingSettingSpinner.isVisible = false
            }
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

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}