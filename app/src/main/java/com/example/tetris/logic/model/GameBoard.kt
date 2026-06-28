package com.example.tetris.logic.model

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
}
