package com.example.tetris.viewModel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class NextBlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)  {

    companion object {
        // 最长有4格 4*4显示
        private const val GRID_SIZE = 4
        /** 每个格子四周留出的空隙，与 GameBoardView 保持一致 */
        private const val CELL_GAP = 1.5f
    }

    /** 格子像素大小 */
    private var cellSize: Float = 0f

    /** 复用 RectF */
    private val cellRect = RectF()

    // 复用GameBoardView的颜色和线条
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF888888.toInt()
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        color = 0xFF000000.toInt()
        style = Paint.Style.STROKE //只画描边线条
        strokeWidth = 0.5f //线条粗细：0.5 像素
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val realL =minOf(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        cellSize = realL.toFloat() / GRID_SIZE
        val total =(cellSize * GRID_SIZE).toInt()
        setMeasuredDimension(total,total)
    }

    override fun onDraw(canvas: Canvas) {
        val totalSize = width.toFloat()

        // 背景
        canvas.drawColor(bgPaint.color)

        drawGridLittelLines(canvas,totalSize)
    }

    private fun drawGridLittelLines(canvas: Canvas, totalSize: Float){
        // 网格线
        for (i in 0..GRID_SIZE) {
            val pos = i * cellSize
            canvas.drawLine(pos, 0f, pos, totalSize, gridPaint)
            canvas.drawLine(0f, pos, totalSize, pos, gridPaint)
        }
    }


}