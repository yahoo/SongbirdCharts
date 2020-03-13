/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.util

import com.yahoo.mobile.android.songbird.model.AudioChartViewModel
import com.yahoo.mobile.android.songbird.model.scaled.ScaledChartData
import com.yahoo.mobile.android.songbird.model.scaled.ScaledPoint
import com.yahoo.mobile.android.songbird.model.scaled.ScaledXAxis
import com.yahoo.mobile.android.songbird.model.scaled.ScaledYAxis
import kotlin.math.absoluteValue

class SongbirdChartViewUtil(
    private val width: Int,
    private val height: Int,
    private val topPadding: Int
) {

    companion object {
        const val LINE_HEIGHT_PERCENTAGE = 0.8f
        const val LINE_WIDTH_PERCENTAGE = 0.85f
        const val Y_AXIS_LABEL_OVERLAP_THRESHOLD = 30
        const val PREVIOUS_CLOSE_TEXT_LEFT_PADDING = 45
        const val LINE_LEFT_PADDING = 30
        const val X_AXIS_HEIGHT_PERCENTAGE = 0.2f
        const val X_AXIS_LEFT_PADDING = 20
        const val X_AXIS_PADDING_RATIO = 3
        const val X_AXIS_HEIGHT_RATIO = 1.9
        private const val PREVIOUS_CLOSE_TEXT_TOP_PADDING = 10
        private const val Y_AXIS_OVERLAP_OFFSET = 40
    }

    private val lineWidth = width * LINE_WIDTH_PERCENTAGE
    private val lineHeight = height * LINE_HEIGHT_PERCENTAGE
    private val chartHeight = lineHeight + topPadding
    private val xAxisHeight = height * X_AXIS_HEIGHT_PERCENTAGE
    private val xAxisPadding = xAxisHeight / X_AXIS_PADDING_RATIO

    fun getChartHeight() = chartHeight

    /**
     * Takes the chart's view model and scales it according to it's inflated height and width
     */
    fun getScaledChartData(chartViewModel: AudioChartViewModel): ScaledChartData {
        with(chartViewModel) {
            val scaledPoints = mutableListOf<ScaledPoint>()
            val xAxises = mutableListOf<ScaledXAxis?>()

            chartDataPoints.forEach { pointViewModel ->
                val x = getScaledX(pointViewModel.index.toLong(), chartDataPoints.last().index.toLong())
                val y = getScaledY(pointViewModel.value.toFloat(), highestValue.toFloat(), valueDelta.toFloat())

                scaledPoints.add(
                    ScaledPoint(
                        x = x,
                        y = y,
                        value = pointViewModel.value
                    )
                )

                xAxises.add(
                    xAxisLabels.find { it.index == pointViewModel.index && showXAxis }?.let {
                        ScaledXAxis(
                            label = it.label,
                            labelX = x + X_AXIS_LEFT_PADDING,
                            labelY = (chartHeight + ((height - chartHeight) / X_AXIS_HEIGHT_RATIO).toInt()),
                            startX = x,
                            startY = height.toFloat() - xAxisPadding,
                            endX = x,
                            endY = chartHeight + xAxisPadding
                        )
                    }
                )
            }

            val lastY = getScaledY(chartDataPoints.last().value.toFloat(), highestValue.toFloat(), valueDelta.toFloat())

            return ScaledChartData(
                benchmarkY = getScaledY(benchmark.toFloat(), highestValue.toFloat(), valueDelta.toFloat()),
                benchmarkWidth = width.toFloat() * if (showYAxis) LINE_WIDTH_PERCENTAGE else 1f,
                points = scaledPoints,
                xAxises = xAxises,
                yAxis = if (showYAxis) generateScaledYAxis(lastY, this) else null
            )
        }
    }

    /**
     * Determines where the y-axis lines should be drawn and the positions of the labels
     * Handles collision of labels within a `Y_AXIS_LABEL_OVERLAP_THRESHOLD` threshold
     */
    private fun generateScaledYAxis(y: Float, chartViewModel: AudioChartViewModel): ScaledYAxis {
        with(chartViewModel) {
            var currentPriceYPosition = y + PREVIOUS_CLOSE_TEXT_TOP_PADDING
            var benchmarkYPosition = getScaledY(benchmark.toFloat(), highestValue.toFloat(), valueDelta.toFloat()) + PREVIOUS_CLOSE_TEXT_TOP_PADDING
            val highPriceYPosition = getScaledY(highestPointValue.toFloat(), highestValue.toFloat(), valueDelta.toFloat()) + PREVIOUS_CLOSE_TEXT_TOP_PADDING
            val lowPriceYPosition = getScaledY(lowestPointValue.toFloat(), highestValue.toFloat(), valueDelta.toFloat()) + PREVIOUS_CLOSE_TEXT_TOP_PADDING

            // If previous close and current price overlap, shift them both away from each other
            if ((currentPriceYPosition - benchmarkYPosition).absoluteValue < Y_AXIS_LABEL_OVERLAP_THRESHOLD) {
                if (currentPriceYPosition < benchmarkYPosition) {
                    benchmarkYPosition += Y_AXIS_OVERLAP_OFFSET
                    currentPriceYPosition -= Y_AXIS_OVERLAP_OFFSET

                } else {
                    benchmarkYPosition -= Y_AXIS_OVERLAP_OFFSET
                    currentPriceYPosition += Y_AXIS_OVERLAP_OFFSET
                }
            }

            return ScaledYAxis(
                priceHint = priceHint.toDouble(),
                previousClose = benchmark,
                previousCloseX = lineWidth + PREVIOUS_CLOSE_TEXT_LEFT_PADDING,
                previousCloseY = benchmarkYPosition,
                currentPrice = chartDataPoints.last().value,
                currentPriceX = lineWidth + PREVIOUS_CLOSE_TEXT_LEFT_PADDING,
                currentPriceY = currentPriceYPosition,
                startX = lineWidth,
                endX = lineWidth + LINE_LEFT_PADDING,
                topY = highPriceYPosition,
                bottomY = lowPriceYPosition,
                drawHighestPrice = (highPriceYPosition - benchmarkYPosition).absoluteValue > Y_AXIS_LABEL_OVERLAP_THRESHOLD && (highPriceYPosition - currentPriceYPosition).absoluteValue > Y_AXIS_LABEL_OVERLAP_THRESHOLD,
                highestPrice = highestPointValue,
                highestPriceX = lineWidth + PREVIOUS_CLOSE_TEXT_LEFT_PADDING,
                highestPriceY = highPriceYPosition,
                drawLowestPrice = (lowPriceYPosition - benchmarkYPosition).absoluteValue > Y_AXIS_LABEL_OVERLAP_THRESHOLD && (lowPriceYPosition - currentPriceYPosition).absoluteValue > Y_AXIS_LABEL_OVERLAP_THRESHOLD,
                lowestPrice = lowestPointValue,
                lowestPriceX = lineWidth + PREVIOUS_CLOSE_TEXT_LEFT_PADDING,
                lowestPriceY = lowPriceYPosition
            )
        }
    }


    private fun getScaledY(price: Float, highestPrice: Float, priceDelta: Float) = (((highestPrice - price) / priceDelta) * lineHeight) + topPadding

    private fun getScaledX(index: Long, lastIndex: Long) = (index / lastIndex.toFloat()) * width
}