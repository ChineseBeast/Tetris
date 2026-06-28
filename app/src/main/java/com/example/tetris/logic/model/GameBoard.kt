package com.example.tetris.logic.model

import android.icu.text.Transliterator.Position

/**
 * 10×20 游戏棋盘数据结构。
 *
 * 内部通过一维 [cells] 存储，索引 = row * [width] + col。
 * null 表示空格，非 null 为对应方块的 ARGB 颜色值。
 */
data class GameBoard(
    val width: Int = BOARD_WIDTH,
    val height: Int = BOARD_HEIGHT,
    val cells: List<Int?>,
) {
    companion object {
        const val BOARD_WIDTH = 10
        const val BOARD_HEIGHT = 20

        /** 创建一个全空的棋盘 */
        fun empty(width: Int = BOARD_WIDTH, height: Int = BOARD_HEIGHT): GameBoard {
            return GameBoard(width, height, MutableList(width * height) { null })
        }
    }

    /** 指定坐标是否在棋盘范围内 */
    fun isInBounds(col: Int, row: Int): Boolean =
        col in 0..<width && row in 0..<height

    /** 返回的某一个位置（row * width + col）的颜色值 */
    operator fun get(col: Int, row: Int): Int? {
        if (col !in 0..width || row !in 0..height) return null
        return cells[row * width + col]
    }

    /** 指定格子是否为空 */
    fun isEmptyAt(col: Int, row: Int): Boolean = get(col, row) == null

    private fun toIndex(col: Int, row: Int): Int = row * width + col
    /** 设置当cell */
    fun setCell(col: Int, row: Int, color: Int): GameBoard {
        val mutable = cells.toMutableList()
        mutable[toIndex(col, row)] = color
        return copy(cells = mutable)
    }
    /** 设置批量cell */
    fun setCells(positions: List<Pair<Int, Int>>, color: Int): GameBoard{
        val mutable = cells.toMutableList()
        for((col,row) in positions){
            mutable[toIndex(col,row)]=color
        }
        return copy(cells = mutable)
    }

    /** 清除填满了的行,并且向下填,返回清除后的Board和清理了几行 */
    fun clearLines():Pair<GameBoard,Int>{
        // cells.chunked(width) 把cell 分成几个长度为width的段
        // remaining就是需要留下了的行
        val remaining = cells.chunked(width).filter { row -> row.any { it == null } }
        val cleared = height - remaining.size
        if (cleared == 0) return this to 0

        val newCells =  mutableListOf<Int?>()
        //从上到下，上面都是消除了的
        for(i in 0..<cleared){
            for(j in 0..<width){
                //都是消除了的
                newCells.add(null)
            }
        }
        for(row in remaining){
            newCells.addAll(row)
        }
        return copy(cells= newCells) to cleared
    }
}
