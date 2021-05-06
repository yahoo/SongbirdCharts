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
import androidx.recyclerview.widget.RecyclerView
import com.yahoo.mobile.android.songbird.player.AudioPlayer
import com.yahoo.mobile.android.songbird.model.ScrubPointViewModel
import com.yahoo.mobile.android.songbird.model.ChartViewModel
import com.yahoo.mobile.android.songbird.util.SettingsHelper
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

    private lateinit var settingsHelper: SettingsHelper
    private var currentX = 0f
    private var scrubTimer: TimerTask? = null
    private val audioPlayer = AudioPlayer(context)
    private val scrubAdapter = ScrubAdapter()
    private val chartView = ChartView(context, attrs, defStyleAttr, defStyleRes).apply {
        id = View.generateViewId()
    }
    private val scrubRecyclerView = ScrubRecyclerView(context, attrs, defStyleAttr).apply {
        id = View.generateViewId()
        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }
    private lateinit var viewModel: ChartViewModel
    private val onScrubListener: ScrubRecyclerView.ScrubCallback = object : ScrubRecyclerView.ScrubCallback {
        override fun onScrub(x: Float) {
            if (currentX != x) {
                scrubTimer?.cancel()
                val index = DimensionUtils.getItemIndexByWidth(context, viewModel.chartDataPoints.size, x)
                playDataPoint(if (index > 0) index - 1 else index)
                scrubTimer = Timer(TIMER_NAME, false).schedule(SCRUB_HOLD_THRESHOLD) {
                    handler.post {
                        focusOnChartPoint(if (index > 0) index - 1 else index)
                    }
                }
                currentX = x
            }
        }

        override fun onRelease(x: Float) {
            if (currentX != x) {
                scrubTimer?.cancel()
                val index = DimensionUtils.getItemIndexByWidth(
                    context,
                    viewModel.chartDataPoints.size,
                    x
                )
                focusOnChartPoint(if (index > 0) index - 1 else index)
            }
        }
    }
    private val configurationUpdateListener: ((SettingsHelper.AccessibleConfiguration) -> Unit) = { configuration ->
        when (configuration) {
            is SettingsHelper.AccessibleConfiguration.TalkBackConfiguration -> {
                updateTalkbackStrings { dataPoint, priceHint ->
                    settingsHelper.getTalkBackString(
                        ValueFormatter.getAsFormattedPrice(dataPoint.value, priceHint.toDouble()),
                        dataPoint.timestamp
                    )
                }
            }
            is SettingsHelper.AccessibleConfiguration.MaximumFrequencyConfiguration -> {
                audioPlayer.setMaxTone(settingsHelper.getMaximumTone())
            }
            is SettingsHelper.AccessibleConfiguration.MinimumFrequencyConfiguration -> {
                audioPlayer.setMinTone(settingsHelper.getMinimumTone())
            }
            is SettingsHelper.AccessibleConfiguration.EchoConfiguration -> {
                audioPlayer.setEchoEnabled(settingsHelper.getEchoChecked())
            }
        }
    }

    init {
        addView(chartView)
        addView(scrubRecyclerView)
        addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                settingsHelper.configurationUpdateListeners.remove(configurationUpdateListener)
            }

            override fun onViewAttachedToWindow(v: View?) {
                settingsHelper = SettingsHelper(context)
                settingsHelper.configurationUpdateListeners.add(configurationUpdateListener)
                audioPlayer.setMaxTone(settingsHelper.getMaximumTone())
                audioPlayer.setMinTone(settingsHelper.getMinimumTone())
                audioPlayer.setEchoEnabled(settingsHelper.getEchoChecked())
            }
        })
    }

    fun setViewModel(chartViewModel: ChartViewModel) {
        this.viewModel = chartViewModel
        with(chartViewModel) {
            audioPlayer.updateLowHighPoints(lowestPointValue, highestPointValue)
            chartView.setChartViewModel(this)
            scrubRecyclerView.onScrubListener = onScrubListener
            scrubRecyclerView.apply {
                layoutManager = GridLayoutManager(context, chartDataPoints.size)
                adapter = scrubAdapter.apply {
                    items = chartDataPoints
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun focusOnChartPoint(index: Int) {
        scrubAdapter.apply {
            focusedIndex = index
            notifyDataSetChanged()
        }
    }

    fun removeFocus() {
        scrubAdapter.apply {
            focusedIndex = -1
            notifyDataSetChanged()
        }
    }

    fun playDataPoint(index: Int) {
        if (index < viewModel.chartDataPoints.size) {
            audioPlayer.onPointFocused(
                viewModel.benchmark,
                viewModel.chartDataPoints[index].value
            )
        }
    }

    fun dispose() {
        audioPlayer.dispose()
    }

    fun playSummaryAudio() {
        audioPlayer.playSummaryAudio(viewModel.benchmark, viewModel.chartDataPoints.map { it.value })
    }

    private fun updateTalkbackStrings(function: (ScrubPointViewModel, Int) -> String) {
        viewModel.apply {
            chartDataPoints.forEach {
                it.talkbackString = function(it, priceHint)
            }
            scrubAdapter.apply {
                items = chartDataPoints
                notifyDataSetChanged()
            }
        }
    }

}