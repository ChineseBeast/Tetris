package com.example.tetris.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.tetris.logic.model.GameBoard

class GameBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        // 10*20 底部10 高度20
        private const val COLS = GameBoard.BOARD_WIDTH   // 10
        private const val ROWS = GameBoard.BOARD_HEIGHT  // 20
        /** 每个格子四周留出的像素空隙，形成白色边框效果 */
        private const val CELL_GAP = 1.5f
    }

    private var board: GameBoard = GameBoard.empty()

    /** 每个方块格子的像素大小，在 onMeasure 中计算 */
    private var cellSize: Float = 0f

    /** 复用 RectF，避免在 onDraw 中频繁创建对象 */
    private val cellRect = RectF()

    // 背景颜色
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF888888.toInt()
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        color = 0xFF000000.toInt()
        style = Paint.Style.STROKE //只画描边线条
        strokeWidth = 0.5f //线条粗细：0.5 像素
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val realWidth = MeasureSpec.getSize(widthMeasureSpec)
        val realHeight = MeasureSpec.getSize(heightMeasureSpec)

        val cellWidth = realWidth.toFloat() / COLS
        val cellHeight = realHeight.toFloat() / ROWS

        cellSize = minOf(cellWidth,cellHeight)


        val boardWidth = (cellSize * COLS).toInt()
        val boardHeigth = (cellSize * ROWS).toInt()


        setMeasuredDimension(boardWidth,boardHeigth)

    }

    override fun onDraw(canvas: Canvas) {
        val totalWidth = width.toFloat()
        val totalHeight = height.toFloat()

        // 画背景颜色
        canvas.drawColor(bgPaint.color)
        // 画网格线
        drawGridLines(canvas, totalWidth, totalHeight)

    }
    /** 绘制浅灰色网格线 */
    private fun drawGridLines(canvas: Canvas, totalWidth: Float, totalHeight: Float) {
        // 垂直线
        for (col in 0..COLS) {
            val x = col * cellSize
            canvas.drawLine(x, 0f, x, totalHeight, gridPaint)
        }
        // 水平线
        for (row in 0..ROWS) {
            val y = row * cellSize
            canvas.drawLine(0f, y, totalWidth, y, gridPaint)
        }
    }


}