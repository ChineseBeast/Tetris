package com.example.tetris.logic.model

data class TetrisCube(
    val type: TetrisCubeType,
    val rotation: Int,
    val boardX: Int,
    val boardY: Int,
) {

    /**
     * 返回该方块所有填充格子在棋盘上的绝对坐标 [列, 行]。
     */
    fun getAbsolutePositions(): List<Pair<Int, Int>> {
        return TetrisCubeType.getShapeOffsets(type, rotation).map { (rowOffset, colOffset) ->
            (boardX + colOffset) to (boardY + rowOffset)
        }
    }
}
