/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.yahoo.mobile.android.songbird.R
import com.yahoo.mobile.android.songbird.extensions.dpToPx
import com.yahoo.mobile.android.songbird.model.AudioChartViewModel
import com.yahoo.mobile.android.songbird.util.SongbirdChartViewUtil
import com.yahoo.mobile.android.songbird.util.ValueFormatter

class AudioChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val STROKE_WIDTH = 5
    }

    private val topPadding = context.resources.getDimension(R.dimen.songbird_chart_top_padding)
    private val gradientOffset = topPadding * 1.7f
    private val interval = arrayOf(10.0f, 10.0f)

    private val fillPaint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = false
        style = Paint.Style.FILL
    }
    private val strokePaint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = false
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = context.resources.dpToPx(2).toFloat()
    }
    private val benchmarkPaint = Paint().apply {
        isAntiAlias = false
        isDither = false
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(FloatArray(2) { interval[it] }, 0.0f)
        strokeWidth = context.resources.dpToPx(2).toFloat()
        color = ContextCompat.getColor(context, R.color.songbird_chart_line)
    }
    private val textPaint = Paint().apply {
        isAntiAlias = false
        isDither = false
        textSize = context.resources.getDimension(R.dimen.regular_text_size)
        strokeWidth = context.resources.dpToPx(5).toFloat()
        color = ContextCompat.getColor(context, R.color.songbird_text)
    }
    private val axisPaint = Paint().apply {
        isAntiAlias = false
        isDither = false
        textSize = context.resources.getDimension(R.dimen.regular_text_size)
        strokeWidth = context.resources.dpToPx(STROKE_WIDTH).toFloat()
        color = ContextCompat.getColor(context, R.color.songbird_chart_line)
    }
    private val axisLinePaint = Paint().apply {
        isAntiAlias = false
        isDither = false
        style = Paint.Style.STROKE
        strokeWidth = context.resources.dpToPx(1).toFloat()
        color = ContextCompat.getColor(context, R.color.songbird_chart_line)
    }
    private val latestValuePaint = Paint().apply {
        isAntiAlias = false
        isDither = false
        textSize = context.resources.getDimension(R.dimen.regular_text_size)
        strokeWidth = context.resources.dpToPx(STROKE_WIDTH).toFloat()
    }
    private val noResultsString = context.getString(R.string.no_result)

    private lateinit var chartViewModel: AudioChartViewModel
    private lateinit var chartViewUtil: SongbirdChartViewUtil

    fun setChartViewModel(chartViewModel: AudioChartViewModel) {
        this.chartViewModel = chartViewModel
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (!::chartViewUtil.isInitialized) {
            chartViewUtil = SongbirdChartViewUtil(width, height, context.resources.getDimensionPixelSize(R.dimen.songbird_chart_top_padding))
        }
        if (::chartViewModel.isInitialized) {
            drawGraph(canvas)
        }
    }

    private fun drawGraph(canvas: Canvas) {
        val strokePath = Path()
        val fillPath = Path()

        if (chartViewModel.chartDataPoints.isEmpty()) {
            canvas.drawText(noResultsString, width / 2.3f, height / 2f, textPaint)
        } else {
            with(chartViewUtil.getScaledChartData(chartViewModel)) {
                // Benchmark
                canvas.drawLine(0f, benchmarkY, benchmarkWidth, benchmarkY, benchmarkPaint)

                // Gradient
                fillPaint.color = ContextCompat.getColor(context, chartViewModel.fillColor)
                strokePaint.color = ContextCompat.getColor(context, chartViewModel.strokeColor)
                latestValuePaint.color = ContextCompat.getColor(context, chartViewModel.strokeColor)
                benchmarkPaint.color = ContextCompat.getColor(context, chartViewModel.benchmarkColor)
                val scaledChartData = chartViewUtil.getScaledChartData(chartViewModel)
                fillPaint.shader = LinearGradient(
                    0.0f,
                    benchmarkY,
                    0.0f,
                    0.0f,
                    ContextCompat.getColor(context, R.color.songbird_chart_background),
                    ContextCompat.getColor(context, chartViewModel.fillColor),
                    Shader.TileMode.MIRROR
                )
                points.forEachIndexed { index, scaledPoint ->
                    xAxises[index]?.let {
                        canvas.drawLine(it.startX, it.startY, it.endX, it.endY, axisLinePaint)
                        canvas.drawText(it.label, it.labelX, it.labelY, axisPaint)
                    }
                    if (index == 0) {
                        fillPath.lineTo(scaledPoint.x, scaledPoint.y)
                        strokePath.moveTo(scaledPoint.x, scaledPoint.y)
                    } else {
                        fillPath.lineTo(scaledPoint.x, scaledPoint.y)
                        strokePath.lineTo(scaledPoint.x, scaledPoint.y)
                        if (index == chartViewModel.chartDataPoints.size - 1) {
                            fillPath.lineTo(scaledPoint.x, chartViewUtil.getChartHeight())
                            fillPath.lineTo(0.0f, chartViewUtil.getChartHeight())
                            canvas.drawPath(
                                strokePath,
                                strokePaint
                            )
                            canvas.drawPath(
                                fillPath,
                                fillPaint
                            )
                            yAxis?.let {
                                // Previous close
                                canvas.drawText(
                                    ValueFormatter.getAsFormattedPrice(it.previousClose, it.priceHint),
                                    it.previousCloseX,
                                    it.previousCloseY,
                                    textPaint
                                )
                                // Current price
                                canvas.drawText(
                                    ValueFormatter.getAsFormattedPrice(it.currentPrice, it.priceHint),
                                    it.currentPriceX,
                                    it.currentPriceY,
                                    latestValuePaint
                                )

                                // If high/low price does not touch previous close and current price, then draw it
                                if (it.drawHighestPrice) {
                                    canvas.drawText(
                                        ValueFormatter.getAsFormattedPrice(it.highestPrice, it.priceHint),
                                        it.highestPriceX,
                                        it.highestPriceY,
                                        axisPaint
                                    )
                                }
                                if (it.drawLowestPrice) {
                                    canvas.drawText(
                                        ValueFormatter.getAsFormattedPrice(it.lowestPrice, it.priceHint),
                                        it.lowestPriceX,
                                        it.lowestPriceY,
                                        axisPaint
                                    )
                                }

                                // Y-axis lines
                                canvas.drawLine(it.startX, it.topY, it.endX, it.topY, axisLinePaint)
                                canvas.drawLine(it.startX, it.topY, it.startX, it.bottomY, axisLinePaint)
                                canvas.drawLine(it.startX, it.bottomY, it.endX, it.bottomY, axisLinePaint)
                            }
                        }
                    }
                }
            }
        }
    }
}