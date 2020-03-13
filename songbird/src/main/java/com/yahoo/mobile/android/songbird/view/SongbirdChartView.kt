/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.yahoo.mobile.android.songbird.AudioChartAudioHelper
import com.yahoo.mobile.android.songbird.model.AudioChartPointViewModel
import com.yahoo.mobile.android.songbird.model.AudioChartViewModel
import com.yahoo.mobile.android.songbird.util.AudioChartSettingsHelper
import com.yahoo.mobile.android.songbird.util.DimensionUtils
import com.yahoo.mobile.android.songbird.util.ValueFormatter
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

class SongbirdChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        const val TIMER_NAME = "OnScrub"
        const val SCRUB_HOLD_THRESHOLD = 250L
    }

    private lateinit var audioChartSettingsHelper: AudioChartSettingsHelper
    private var currentX = 0f
    private var timer: TimerTask? = null
    private val audioChartAudioHelper = AudioChartAudioHelper(
        AudioChartSettingsHelper(
            context
        )
    )
    private val audioPointsAdapter = AudioPointsAdapter()
    private val audioChartView = AudioChartView(context, attrs, defStyleAttr, defStyleRes).apply {
        id = View.generateViewId()
    }
    private val audioRecyclerView = AudioRecyclerView(context, attrs, defStyleAttr).apply {
        id = View.generateViewId()
    }
    private lateinit var chartViewModel: AudioChartViewModel
    private val onScrubListener: AudioRecyclerView.ScrubCallback = object : AudioRecyclerView.ScrubCallback {
        override fun onScrub(x: Float) {
            if (currentX != x) {
                timer?.cancel()
                val index = DimensionUtils.getItemIndexByWidth(context, chartViewModel.chartDataPoints.size, x)
                playChartPoint(if (index > 0) index - 1 else index)
                timer = Timer(TIMER_NAME, false).schedule(SCRUB_HOLD_THRESHOLD) {
                    handler.post {
                        focusOnChartPoint(if (index > 0) index - 1 else index)
                    }
                }
                currentX = x
            }
        }

        override fun onRelease(x: Float) {
            if (currentX != x) {
                timer?.cancel()
                val index = DimensionUtils.getItemIndexByWidth(
                    context,
                    chartViewModel.chartDataPoints.size,
                    x
                )
                focusOnChartPoint(if (index > 0) index - 1 else index)
            }
        }
    }
    private val configurationUpdateListener: ((AudioChartSettingsHelper.AccessibleConfiguration) -> Unit) = { configuration ->
        if (configuration is AudioChartSettingsHelper.AccessibleConfiguration.TalkBackConfiguration) {
            updateTalkbackStrings { dataPoint, priceHint ->
                audioChartSettingsHelper.getTalkBackString(
                    ValueFormatter.getAsFormattedPrice(dataPoint.value, priceHint.toDouble()),
                    dataPoint.timestamp
                )
            }
        }
    }

    init {
        addView(audioChartView)
        addView(audioRecyclerView)
        addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                audioChartSettingsHelper.configurationUpdateListeners.remove(configurationUpdateListener)
            }

            override fun onViewAttachedToWindow(v: View?) {
                audioChartSettingsHelper = AudioChartSettingsHelper(context)
                audioChartSettingsHelper.configurationUpdateListeners.add(configurationUpdateListener)
            }
        })
    }

    fun setViewModel(chartViewModel: AudioChartViewModel) {
        this.chartViewModel = chartViewModel
        with(chartViewModel) {
            audioChartAudioHelper.updateMinMax(lowestPointValue, highestPointValue)
            audioChartView.setChartViewModel(this)
            audioRecyclerView.onScrubListener = onScrubListener
            audioRecyclerView.apply {
                layoutManager = GridLayoutManager(context, chartDataPoints.size)
                adapter = audioPointsAdapter.apply {
                    items = chartDataPoints
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun focusOnChartPoint(index: Int) {
        audioPointsAdapter.apply {
            focusedIndex = index
            notifyDataSetChanged()
        }
    }

    fun removeFocus() {
        audioPointsAdapter.apply {
            focusedIndex = -1
            notifyDataSetChanged()
        }
    }

    fun playChartPoint(index: Int) {
        audioChartAudioHelper.onPointFocused(chartViewModel.benchmark, chartViewModel.chartDataPoints[index].value)
    }

    fun dispose() {
        audioChartAudioHelper.dispose()
    }

    fun playSummaryAudio() {
        audioChartAudioHelper.playSummaryAudio(chartViewModel.benchmark, chartViewModel.chartDataPoints.map { it.value })
    }

    private fun updateTalkbackStrings(function: (AudioChartPointViewModel, Int) -> String) {
        chartViewModel.apply {
            chartDataPoints.forEach {
                it.talkbackString = function(it, priceHint)
            }
            audioPointsAdapter.apply {
                items = chartDataPoints
                notifyDataSetChanged()
            }
        }
    }

}