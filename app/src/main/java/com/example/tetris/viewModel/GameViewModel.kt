package com.example.tetris.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.GameState
import com.example.tetris.logic.model.GameBoard
import com.example.tetris.logic.model.TetrisCube
import com.example.tetris.logic.model.TetrisCubeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    // 标准写法
    private val _gameBoard = MutableStateFlow(GameBoard.empty())
    val gameBoard: StateFlow<GameBoard> = _gameBoard.asStateFlow()

    private val _currentTetrisCube = MutableStateFlow<TetrisCube?>(null)
    val currentTetrisCube: StateFlow<TetrisCube?> = _currentTetrisCube.asStateFlow()

    private val _nextTetrisCube = MutableStateFlow<TetrisCube?>(null)
    val nextTetrisCube: StateFlow<TetrisCube?> = _nextTetrisCube.asStateFlow()

    /** 当前使用的随机源，可通过种子控制来测试 */
    private var random: Random = Random.Default

    /** 自动下落协程 */
    private var gameLoopJob: Job? = null

    /** 开启循环游戏 */
    fun startGame() {
        startGameLoop()
    }

    /** 生成的方块置于棋盘顶部中央 */
    fun spawnTetrisCube(){
        if(_nextTetrisCube.value == null){
            //第一次调用就先 生成两个方块
            _nextTetrisCube.value = createRandomTetrisCube()
        }

        val currentCube = _nextTetrisCube.value ?: return
        val spawnX = calculateSpawnX(currentCube.type)
        val createCube = currentCube.copy(boardX = spawnX, boardY = 0)
        _currentTetrisCube.value = createCube
        //这里将下一个创建出来
        _nextTetrisCube.value = createRandomTetrisCube()
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

    /** 左移 */
    fun moveLeft(){
        val cube = _currentTetrisCube.value ?: return
        val moved = cube.movedBy(-1,0)
        if(isValidPosition(moved,_gameBoard.value)){
            _currentTetrisCube.value = moved
        }
    }

    /** 右移 */
    fun moveRight(){
        val cube = _currentTetrisCube.value ?: return
        val moved = cube.movedBy(1,0)
        if(isValidPosition(moved,_gameBoard.value)){
            _currentTetrisCube.value = moved
        }
    }

    /** 向下移动 */
    fun moveDown(): Boolean{
        val currentCube = _currentTetrisCube.value ?: return false
        // 向下移动y+1
        val moved = currentCube.movedBy(0,1)
        // 判断位置是否合法
        if(isValidPosition(moved, _gameBoard.value)){
            _currentTetrisCube.value = moved
            return true
        }
        lockAndSpawn()
        return false
    }

    /** 检测指定方块在棋盘上是否处于合法位置 */
    fun isValidPosition(cube: TetrisCube, board: GameBoard): Boolean{
        return cube.getAbsolutePositions().all{(col,row)->
            board.isInBounds(col,row)&& board.isEmptyAt(col,row)
        }
    }

    /** 将当前方块锁定到棋盘上 */
    fun lockAndSpawn(){
        val currentCube = _currentTetrisCube.value ?: return
        val board = _gameBoard.value

        val updatedBoard =board.setCells(currentCube.getAbsolutePositions(), currentCube.type.color)
        _gameBoard.value = updatedBoard
        // 清除方块进行生成下一个
        _currentTetrisCube.value = null
        spawnTetrisCube()
    }

    /** 开启游戏循环 */
    private fun startGameLoop(){
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch(Dispatchers.Default) {
            while(true){
                delay(500) // 每 500ms 下落一格
                moveDown()
            }
        }
    }
}