package com.example.tetris

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tetris.databinding.ActivityMainBinding
//做内核
class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //layoutInflater用于加载xml布局文件，转化为实际的View对象
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}