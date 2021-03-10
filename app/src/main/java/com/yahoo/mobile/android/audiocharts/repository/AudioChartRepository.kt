package com.yahoo.mobile.android.audiocharts.repository

import com.yahoo.mobile.android.audiocharts.R
import com.yahoo.mobile.android.audiocharts.base.ApplicationBase
import com.yahoo.mobile.android.songbird.model.ScrubPointViewModel
import com.yahoo.mobile.android.songbird.model.ChartViewModel
import com.yahoo.mobile.android.songbird.model.XAxisLabel
import com.yahoo.mobile.android.songbird.util.SettingsHelper
import com.yahoo.mobile.android.songbird.util.DateTimeUtils
import com.yahoo.mobile.android.songbird.util.ValueFormatter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object AudioChartRepository {

    private const val NUMBER_OF_POINTS = 15
    private val audioChartSettingsHelper = SettingsHelper(ApplicationBase.instance)

    /**
     * Sample Songbird chart for demo purposes
     */
    fun loadSampleSongbirdChart(showXAxis: Boolean, showYAxis: Boolean): ChartViewModel {
        val points = generateSamplePoints()
        val benchmark = points.random().value
        return ChartViewModel(
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
            fillColor = if (points.last().value > benchmark) R.color.songbird_chart_pos_fill else R.color.songbird_chart_neg_fill,
            strokeColor = if (points.last().value > benchmark) R.color.songbird_chart_pos_stroke else R.color.songbird_chart_neg_stroke,
            benchmarkColor = R.color.songbird_chart_line
        )
    }

    private fun generateSamplePoints(): List<ScrubPointViewModel> {
        val points = mutableListOf<ScrubPointViewModel>()
        val startTime = System.currentTimeMillis()
        for (i in 0 until NUMBER_OF_POINTS) {
            val value = Random.nextDouble(0.0, 100.0)
            val timestamp = startTime + TimeUnit.MINUTES.toMillis(i.toLong())
            points.add(
                ScrubPointViewModel(
                    value = value,
                    timestamp = timestamp,
                    index = i,
                    talkbackString = audioChartSettingsHelper.getTalkBackString(
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