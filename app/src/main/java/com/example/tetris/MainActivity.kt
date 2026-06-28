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
import com.example.myapplication.data.model.GameState
import com.example.tetris.databinding.ActivityMainBinding
import com.example.tetris.viewModel.GameViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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
        // 得分情况
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.score.collect { score->
                    binding.tvScore.text = score.toString()
                }
            }
        }
        // 花费时间
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.spendTime.collect { seconds ->
                    val h = seconds / 3600
                    val m = (seconds % 3600) / 60
                    val s = seconds % 60
                    binding.tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
                }
            }
        }

        // 绑定按钮事件
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.btnDown.setOnClickListener { viewModel.moveDown() }
        binding.btnLeft.setOnClickListener{ viewModel.moveLeft() }
        binding.btnRight.setOnClickListener{ viewModel.moveRight() }
        binding.btnRotate.setOnClickListener{ viewModel.rotate() }
        binding.btnHardDrop.setOnClickListener{ viewModel.hardDrop() }

        // 开始 暂停按钮
        binding.btnCenter.setOnClickListener {
            when(viewModel.gamePhase.value){
                is GameState.Playing-> viewModel.togglePause()
                is GameState.Paused -> viewModel.togglePause()
                is GameState.Idle, is GameState.GameOver ->{
                    viewModel.startGame()
                }

            }
        }
    }
}