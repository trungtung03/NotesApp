package com.example.notepad.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View


class ColumnChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val chartValues = mutableListOf<Triple<String, Float, Int>>()
    private val charTitle = mutableListOf<Double>()


    fun setData(values: List<Triple<String, Float, Int>>, chartMoney: List<Double>) {
        chartValues.clear()
        chartValues.addAll(values)
        this.charTitle.clear()
        charTitle.addAll(chartMoney)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        var index: Int = 0
        val chartWidth = width
        val chartHeight = height
        val columnCount = chartValues.size

        if (columnCount == 0) return

        var columnWidth = chartWidth / columnCount.toFloat() / 1.36f

        val columnSpacing = columnWidth / columnCount.toFloat() // khoảng cách giữa các cột
        var x = columnSpacing / 2
        chartValues.forEach { (label, value, color) ->
            val columnHeight = chartHeight * (value / 130)

            // Vẽ cột
            val paint = Paint().apply {
                this.color = color
                style = Paint.Style.FILL
            }

            canvas?.drawRect(
                x, chartHeight - columnHeight,
                x + columnWidth, chartHeight.toFloat(),
                paint
            )
            // Vẽ label
            val textPaint = Paint().apply {
                this.color = Color.BLACK
                textSize = 28f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.LEFT
            }
            val valueTextBounds = Rect().apply {
                textPaint.getTextBounds(label, 0, label.length, this)
            }
            val valueTextX = x + (columnWidth / 2) - (valueTextBounds.width() / 2)
            val valueTextY = chartHeight - columnHeight - valueTextBounds.height() - 36

            canvas?.drawText(label, valueTextX, valueTextY, textPaint)

            // Vẽ giá trị trên cột
            val valueText = (charTitle.getOrNull(index) ?: 0).toString().plus(" Tr")
            val textPaintTile = Paint().apply {
                this.color = Color.BLACK
                textSize = 24f
                textAlign = Paint.Align.LEFT

            }
            val valueTextBottom = Rect().apply {
                textPaint.getTextBounds(valueText, 0, valueText.length, this)
            }

            val valueBottomX = x + (columnWidth / 2) - (valueTextBottom.width() / 2)
            val valueBottomY = chartHeight - columnHeight - valueTextBottom.height()

            canvas?.drawText(valueText, valueBottomX, valueBottomY, textPaintTile)

            // Tính toán giá trị x cho cột tiếp theo
            x += columnWidth + columnSpacing
            index++
        }
    }


}
