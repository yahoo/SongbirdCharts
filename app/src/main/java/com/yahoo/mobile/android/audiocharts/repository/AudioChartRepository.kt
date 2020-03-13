package com.yahoo.mobile.android.audiocharts.repository

import com.yahoo.mobile.android.audiocharts.R
import com.yahoo.mobile.android.audiocharts.base.ApplicationBase
import com.yahoo.mobile.android.songbird.model.AudioChartPointViewModel
import com.yahoo.mobile.android.songbird.model.AudioChartViewModel
import com.yahoo.mobile.android.songbird.model.XAxisLabel
import com.yahoo.mobile.android.songbird.util.AudioChartSettingsHelper
import com.yahoo.mobile.android.songbird.util.DateTimeUtils
import com.yahoo.mobile.android.songbird.util.ValueFormatter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object AudioChartRepository {

    private const val NUMBER_OF_POINTS = 15
    private val audioChartSettingsHelper = AudioChartSettingsHelper(ApplicationBase.instance)

    /**
     * Sample Songbird chart for demo purposes
     */
    fun loadSampleSongbirdChart(showXAxis: Boolean, showYAxis: Boolean): AudioChartViewModel {
        val points = generateSamplePoints()
        val benchmark = points.random().value
        return AudioChartViewModel(
            contentDescription = "",
            benchmark = benchmark,
            chartDataPoints = points,
            priceHint = 2,
            showXAxis = showXAxis,
            showYAxis = showYAxis,
            xAxisLabels = listOf(
                XAxisLabel(
                    index = 0,
                    label = DateTimeUtils.getTimeOnly(points[0].timestamp)
                ),
                XAxisLabel(
                    index = NUMBER_OF_POINTS / 3,
                    label = DateTimeUtils.getTimeOnly(points[NUMBER_OF_POINTS / 3].timestamp)
                ),
                XAxisLabel(
                    index = NUMBER_OF_POINTS - 5,
                    label = DateTimeUtils.getTimeOnly(points[NUMBER_OF_POINTS - 5].timestamp)
                )
            ),
            fillColor = if (points.last().value > benchmark) R.color.chart_pos_fill else R.color.chart_neg_fill,
            strokeColor = if (points.last().value > benchmark) R.color.chart_pos_stroke else R.color.chart_neg_stroke,
            benchmarkColor = R.color.chart_line
        )
    }

    private fun generateSamplePoints(): List<AudioChartPointViewModel> {
        val points = mutableListOf<AudioChartPointViewModel>()
        val startTime = System.currentTimeMillis()
        for (i in 0 until NUMBER_OF_POINTS) {
            val value = Random.nextDouble()
            val timestamp = startTime + TimeUnit.MINUTES.toMillis(i.toLong())
            points.add(
                AudioChartPointViewModel(
                    value = value,
                    timestamp = timestamp,
                    index = i,
                    defaultTalkbackString = audioChartSettingsHelper.getTalkBackString(
                        formattedPrice = ValueFormatter.getAsFormattedPrice(value, 2.0),
                        timestampMs = timestamp
                    )
                ) {
                    // On Swipe Callback
                }
            )
        }
        return points
    }

}