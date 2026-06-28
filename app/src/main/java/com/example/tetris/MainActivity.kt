package com.example.tetris

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.tetris.databinding.ActivityMainBinding
import com.example.tetris.viewModel.GameViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 创建 ViewModel
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        // 生成一个方块，显示在棋盘上
        viewModel.spawnTetrisCube()

        // 收集状态变化，驱动 GameBoardView 重绘
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentTetrisCube.collect { cube ->
                    binding.gameBoardView.updateData(
                        board = viewModel.gameBoard.value,
                        currentCube = cube
                    )
                }
            }
        }
    }
}