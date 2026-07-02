package com.example.tetris.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.GameState
import com.example.tetris.logic.model.GameBoard
import com.example.tetris.logic.model.TetrisCube
import com.example.tetris.logic.model.TetrisCubeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    // 标准写法 私有内部可变 MutableLiveData
    private val _gameBoard = MutableLiveData(GameBoard.empty())
    // 公开只读 LiveData
    val gameBoard: LiveData<GameBoard> = _gameBoard

    private val _currentTetrisCube = MutableLiveData<TetrisCube?>(null)
    val currentTetrisCube: LiveData<TetrisCube?> = _currentTetrisCube

    private val _nextTetrisCube = MutableLiveData<TetrisCube?>(null)
    val nextTetrisCube: LiveData<TetrisCube?> = _nextTetrisCube

    private val _gamePhase = MutableLiveData<GameState>(GameState.Idle)
    val gamePhase :LiveData<GameState> = _gamePhase

    // 用于GameOver状态
    private val _score = MutableLiveData(0)
    val score : LiveData<Int> = _score

    // 游戏难度
    private val _level = MutableLiveData(1)
    val level: LiveData<Int> = _level

    private val _spendTime = MutableLiveData(0)
    val spendTime = _spendTime
    /** 当前使用的随机源，可通过种子控制来测试 */
    private var random: Random = Random.Default

    /** 自动下落协程 */
    private var gameLoopJob: Job? = null

    /** 倒计时协程 */
    private var timerJob: Job? = null

    /** 开启循环游戏 */
    fun startGame() {
        gameLoopJob?.cancel()
        timerJob?.cancel()
        _gameBoard.value = GameBoard.empty()
        _gamePhase.value =GameState.Playing
        _score.value = 0
        _level.value = 1
        _spendTime.value = 0

        spawnTetrisCube()
        startGameLoop()
        startTimer()
    }

    /** 计时器 */
    private fun startTimer(){
        timerJob?.cancel()
        _spendTime.postValue(0)
        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive){
                delay(1000)
                if(_gamePhase.value is GameState.Playing){
                    _spendTime.postValue((_spendTime.value ?: 0) + 1)
                }
            }
        }
    }
    /** 加分机制 */
    private fun addScore(lineCleared: Int){
        //消灭1行100分，2行300，3行600，4行1000
        val lineScores = mapOf(1 to 100,2 to 300,3 to 600,4 to 1000)
        val gained = lineScores[lineCleared] ?: (lineCleared * 100)
        val newScore = (_score.value ?: 0) + gained
        _score.postValue(newScore)

        //每10行升一级
        val levelNow = (newScore / 1000).coerceAtLeast(1)
        _level.postValue(levelNow)
    }

    //中间按钮切换开始暂停
    fun togglePause(){
        when(_gamePhase.value){
            is GameState.Playing ->{
                _gamePhase.value = GameState.Paused
            }
            is GameState.Paused ->{
                _gamePhase.value = GameState.Playing
            }
            else -> { /** GameOver/Idle 无需操作 */}
        }
    }

    /** 切屏/跳转时自动暂停 */
    fun pauseOnBackground() {
        if (_gamePhase.value is GameState.Playing) {
            _gamePhase.value = GameState.Paused
        }
    }

    /** 生成的方块置于棋盘顶部中央 */
    fun spawnTetrisCube(board: GameBoard = _gameBoard.value!!){
        val nextCube = _nextTetrisCube.value ?: createRandomTetrisCube().also {
            _nextTetrisCube.postValue(it)
        }

        val spawnX = calculateSpawnX(nextCube.type)
        val createCube = nextCube.copy(boardX = spawnX, boardY = 0)

        //这里需要判断一下 刚刚生成的方块是不是触顶 是否可以放置 不行则GameOver了
        if(!isValidPosition(createCube,board)){
            //游戏结束关闭一些协程
            gameLoopJob?.cancel()
            // GameOver 加了对应分数
            _gamePhase.postValue(GameState.GameOver(_score.value ?: 0))
            _currentTetrisCube.postValue(null)
            _nextTetrisCube.postValue(null)
            return

        }

        _currentTetrisCube.postValue(createCube)
        //这里将将下一个方块存入 LiveData
        _nextTetrisCube.postValue(createRandomTetrisCube())
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
        if(isValidPosition(moved,_gameBoard.value ?: return)){
            _currentTetrisCube.value = moved
        }
    }

    /** 右移 */
    fun moveRight(){
        val cube = _currentTetrisCube.value ?: return
        val moved = cube.movedBy(1,0)
        if(isValidPosition(moved,_gameBoard.value ?: return)){
            _currentTetrisCube.value = moved
        }
    }

    /** 顺时针旋转 */
    fun rotate(){
        val cube = _currentTetrisCube.value?: return
        val rotated = cube.rotateClockwise()
        if(isValidPosition(rotated,_gameBoard.value ?: return)){
            _currentTetrisCube.value = rotated
        }
    }

    fun hardDrop(){
        var cube = _currentTetrisCube.value?: return
        while(true){
            val next = cube.movedBy(0,1)
            if(!isValidPosition(next,_gameBoard.value ?: return))break
            cube = next
        }
        _currentTetrisCube.value = cube
        // 固定下来
        lockAndSpawn()
    }

    /** 向下移动 */
    fun moveDown(): Boolean{
        val currentCube = _currentTetrisCube.value ?: return false
        // 向下移动y+1
        val moved = currentCube.movedBy(0,1)
        // 判断位置是否合法
        if(isValidPosition(moved, _gameBoard.value ?: return false)){
            _currentTetrisCube.postValue(moved)
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
        val board = _gameBoard.value ?: return

        val updatedBoard =board.setCells(currentCube.getAbsolutePositions(), currentCube.type.color)
        _gameBoard.postValue(updatedBoard)

        val (clearBoard,clearLineNum) = updatedBoard.clearLines()
        if(clearLineNum>0){
            _gameBoard.postValue(clearBoard)
            addScore(clearLineNum)
        }

        // 进行生成下一个
        _currentTetrisCube.postValue(null)
        spawnTetrisCube(board = if (clearLineNum > 0) clearBoard else updatedBoard)
    }

    /** 根据等级来看下降速度 */
    private fun delayByLevel(): Long{
//        var level = _level.value
//        if (level < 1) level = 1
//        if (level > 20) level = 20
//        // 每升一个level就减50ms
//        var time = 1000 - (level - 1) * 50
//        if (time < 100) time = 100
//        return time.toLong()

        val level = (_level.value ?: 1).coerceIn(1, 20)
        return (1000L - (level - 1) * 50L).coerceAtLeast(100L)
    }


    /** 开启游戏循环 */
    private fun startGameLoop(){
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch(Dispatchers.Default) {
            while(isActive){
                delay(delayByLevel())
                //当只有是游戏ing状态才进行下落
                if (_gamePhase.value is GameState.Playing) {
                    moveDown()
                }
            }
        }
    }
}