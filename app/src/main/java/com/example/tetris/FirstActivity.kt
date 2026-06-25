package com.example.tetris

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tetris.databinding.ActivityFirstBinding
//做首页
class FirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        // 设置不显示应用名
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.btnPlay.setOnClickListener{
            Toast.makeText(this,"你点击了开始游戏",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnMore.setOnClickListener{
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }

    }

    // 显示menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    // 选择某个item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> Toast.makeText(this, "You clicked Add",
                Toast.LENGTH_SHORT).show()
            R.id.remove_item -> Toast.makeText(this, "You clicked Remove",
                Toast.LENGTH_SHORT).show()
        }
        return true
    }
}