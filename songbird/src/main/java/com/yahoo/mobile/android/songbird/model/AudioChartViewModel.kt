/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.model

import androidx.annotation.ColorRes

class AudioChartViewModel(
    val contentDescription: String,
    val benchmark: Double,
    val chartDataPoints: List<AudioChartPointViewModel>,
    val priceHint: Int,
    val showXAxis: Boolean = true,
    val xAxisLabels: List<XAxisLabel> = listOf(),
    val showYAxis: Boolean = true,
    @ColorRes
    val fillColor: Int,
    @ColorRes
    val strokeColor: Int,
    @ColorRes
    val benchmarkColor: Int
) {
    // Only the highest point on the chart, not the benchmark
    val highestPointValue: Double = chartDataPoints.maxBy { it.value }?.value ?: 0.0
    // The lowest point on the chart, not the benchmark
    val lowestPointValue: Double = chartDataPoints.minBy { it.value }?.value ?: 0.0
    // Either the highest point on the chart line or the benchmark, whichever is higher
    val highestValue: Double = maxOf(highestPointValue, benchmark)

    val valueDelta: Double = highestPointValue - lowestPointValue
}