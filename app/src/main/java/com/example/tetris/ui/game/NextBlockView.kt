package com.example.tetris.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.tetris.logic.model.TetrisCube
import com.example.tetris.logic.model.TetrisCubeType

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

    private var nextCube: TetrisCube? = null

    /** 格子像素大小 */
    private var cellSize: Float = 0f

    /** 复用 RectF */
    private val cellRect = RectF()

    /** 方块填充 */
    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

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
        drawNextCube(canvas)
    }

    private fun drawNextCube(canvas: Canvas){
        nextCube?.let { cube->
            val offsets = TetrisCubeType.getShapeOffsets(cube.type,0)
            // 计算偏移 在4*4居中显示
            val boxSize = cube.type.boundingBoxSize
            val offsetX = (GRID_SIZE - boxSize) / 2
            val offsetY = (GRID_SIZE - boxSize) / 2

            for ((rowOffset, colOffset) in offsets){
                val col = offsetX + colOffset
                val row = offsetY + rowOffset
                if(col in 0..<GRID_SIZE && row in 0..<GRID_SIZE){
                    drawCell(canvas,col,row,cube.type.color)
                }
            }
        }
    }

    //跟他
    private fun drawCell(canvas: Canvas, col: Int, row: Int, color: Int) {
        val rect = cellRect
        rect.left = col * cellSize + CELL_GAP
        rect.top = row * cellSize + CELL_GAP
        rect.right = (col + 1) * cellSize - CELL_GAP
        rect.bottom = (row + 1) * cellSize - CELL_GAP

        cellPaint.color = color
        canvas.drawRect(rect, cellPaint)
    }
    private fun drawGridLittelLines(canvas: Canvas, totalSize: Float){
        // 网格线
        for (i in 0..GRID_SIZE) {
            val pos = i * cellSize
            canvas.drawLine(pos, 0f, pos, totalSize, gridPaint)
            canvas.drawLine(0f, pos, totalSize, pos, gridPaint)
        }
    }

    /** 更新下一个方块数据并触发重绘 */
    fun updatePiece(cube: TetrisCube?) {
        nextCube = cube
        invalidate()
    }
}