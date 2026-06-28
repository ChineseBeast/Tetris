package com.example.tetris

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tetris.databinding.ActivityMainBinding
import com.example.tetris.viewModel.GameViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

//做内核
class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GameViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //layoutInflater用于加载xml布局文件，转化为实际的View对象
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 创建 ViewModel
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        // 生成一个方块，显示在棋盘上
        viewModel.spawnTetrisCube()
        viewModel.startGame()
        // 收集状态变化，驱动 GameBoardView 重绘
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.gameBoard,
                    viewModel.currentTetrisCube,
                ) { board, piece ->
                    Pair(board, piece)
                }.collect { (board, piece) ->
                    binding.gameBoardView.updateData(board, piece)
                }
            }
        }
        // 收集下一个方块，推送给 NextBlockView
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nextTetrisCube.collect { next ->
                    binding.nextBlockView.updatePiece(next)
                }
            }
        }
    }
}