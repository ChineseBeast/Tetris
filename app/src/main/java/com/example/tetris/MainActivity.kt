package com.example.tetris

import android.content.Intent
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
import com.example.tetris.logic.local.Database.TetrisDatabase
import com.example.tetris.logic.local.Entity.GameRecord
import com.example.tetris.viewModel.GameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        // 等待GameOver状态进行
        observeGamePhase()
        // 绑定按钮事件
        setupButtonListeners()
    }
    private fun observeGamePhase() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.gamePhase.collect(){phase->
                    val playing = phase is GameState.Playing
                    setControlsEnabled(playing)
                    when (phase){
                        is GameState.Idle -> {
                            binding.btnCenter.text = "开"
                            binding.gameOverOverlay.visibility = android.view.View.GONE
                        }
                        is GameState.Playing -> {
                            binding.btnCenter.text = "关"
                            binding.gameOverOverlay.visibility = android.view.View.GONE
                        }
                        is GameState.Paused -> {
                            binding.btnCenter.text = "开"
                        }
                        is GameState.GameOver -> {
                            binding.btnCenter.text = "开"
                            // gameOver状态下是保存了score的
                            val score = phase.score
                            binding.tvGameOverScore.text = "分数: ${score.toString()}"
                            binding.gameOverOverlay.visibility = android.view.View.VISIBLE
                            //将GameOver的数据加载到Room数据库
                            lifecycleScope.launch {
                                val db = withContext(Dispatchers.IO){
                                    TetrisDatabase.getInstance(this@MainActivity)
                                }
                                val spendTime = viewModel.spendTime.value
                                val score = viewModel.score.value
                                db.gameRecordDao().insert(
                                    GameRecord(
                                        playDate = System.currentTimeMillis(),
                                        duration = spendTime,
                                        score = score,
                                        note = ""
                                    )
                                )
                            }
                        }
                    }

                }
            }
        }
    }


    /** 在非 Playing（Idle / Paused / GameOver）状态下禁用方向按钮 */
    private fun setControlsEnabled(enabled: Boolean) {
        val buttons = listOf(
            binding.btnDown, binding.btnLeft,
            binding.btnRight, binding.btnRotate,
            binding.btnHardDrop
        )
        buttons.forEach { it.isEnabled = enabled }
        buttons.forEach { it.alpha = if (enabled) 1.0f else 0.4f }
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

        binding.btnRestart.setOnClickListener{
            viewModel.startGame()
        }
        //显示跳转到recordActivity
        binding.btnMore.setOnClickListener {
            val intent = Intent(this,RecordActivity::class.java)
            startActivity(intent)
        }
    }
}