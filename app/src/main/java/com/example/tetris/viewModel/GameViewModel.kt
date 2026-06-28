package com.example.tetris.viewModel

import androidx.lifecycle.ViewModel
import com.example.tetris.logic.model.GameBoard
import com.example.tetris.logic.model.TetrisCube
import com.example.tetris.logic.model.TetrisCubeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class GameViewModel : ViewModel() {

    // 标准写法
    private val _gameBoard = MutableStateFlow(GameBoard.empty())
    val gameBoard: StateFlow<GameBoard> = _gameBoard.asStateFlow()

    private val _currentTetrisCube = MutableStateFlow<TetrisCube?>(null)
    val currentTetrisCube: StateFlow<TetrisCube?> = _currentTetrisCube.asStateFlow()

    /** 当前使用的随机源，可通过种子控制来测试 */
    private var random: Random = Random.Default

    /** 生成的方块置于棋盘顶部中央 */
    fun spawnTetrisCube(){
        val currentCube = createRandomTetrisCube()
        val spawnX = calculateSpawnX(currentCube.type)
        val createCube = currentCube.copy(boardX = spawnX, boardY = 0)
        _currentTetrisCube.value = createCube
    }
    /** 计算方块居中生成所需的 X 坐标 */
    private fun calculateSpawnX(type: TetrisCubeType): Int {
        return (GameBoard.BOARD_WIDTH - type.boundingBoxSize) / 2
    }


    /** 生成一个随机方块 */
    private fun createRandomTetrisCube():TetrisCube{
        val types = TetrisCubeType.entries
        val type = types[random.nextInt(types.size)]
        return TetrisCube(
            type = type,
            rotation = 0,
            boardX = 0,
            boardY = 0,
        )
    }
}