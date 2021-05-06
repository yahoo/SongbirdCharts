/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.player

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.yahoo.mobile.android.songbird.R
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

class AudioPlayer(
    context: Context
) {

    companion object {
        const val TIMER_NAME = "Summary"
        const val TIMER_DELAY = 250L
        const val ECHO_RATE = 0.7f
        val tones = intArrayOf(
            R.raw.c1sharp,
            R.raw.d1sharp,
            R.raw.f1sharp,
            R.raw.g1sharp,
            R.raw.a1sharp,
            R.raw.c2sharp,
            R.raw.d2sharp,
            R.raw.f2sharp,
            R.raw.g2sharp,
            R.raw.a2sharp,
            R.raw.c3sharp,
            R.raw.d3sharp,
            R.raw.f3sharp,
            R.raw.g3sharp,
            R.raw.a3sharp,
            R.raw.c4sharp,
            R.raw.d4sharp,
            R.raw.f4sharp,
            R.raw.g4sharp,
            R.raw.a4sharp,
            R.raw.c5sharp,
            R.raw.d5sharp,
            R.raw.f5sharp,
            R.raw.g5sharp,
            R.raw.a5sharp
        )
    }

    private var high = 0.0
    private var low = 0.0
    private var maxTone = tones.size-1
    private var minTone = 0
    private var echoEnabled = true

    private var timer: TimerTask? = null
    private var currentY = 0.0
    private val soundIds = mutableListOf<Int>()
    private var soundPool: SoundPool? = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    init {
        for (tone in tones) {
            soundIds.add(
                soundPool?.load(context, tone, 0) ?: 0
            )
        }
    }

    private fun playToneAtGivenProgress(benchmark: Double, y: Double) {
        soundPool?.play(soundIds[getIndexFromY(y)], 1f, 1f, 0, 0, if (y < benchmark && echoEnabled) ECHO_RATE else 1f)
    }

    internal fun getIndexFromY(y: Double): Int {
        val percentage = (y - low) / (high - low)
        val index = ((tones.size-1) * percentage).toInt()
        return when {
            index > maxTone -> {
                maxTone
            }
            index < minTone -> {
                minTone
            }
            else -> {
                index
            }
        }
    }

    fun playSummaryAudio(previousClose: Double, points: List<Double>) {
        timer?.cancel()
        var summaryIndex = 0
        timer = Timer(TIMER_NAME, false).schedule(0, TIMER_DELAY) {
            if (summaryIndex < points.size) {
                playToneAtGivenProgress(previousClose, points[summaryIndex++])
            }
        }
    }

    fun onPointFocused(previousClose: Double, y: Double) {
        if (currentY != y) {
            playToneAtGivenProgress(previousClose, y)
            currentY = y
        }
    }

    fun updateLowHighPoints(low: Double, high: Double) {
        this.high = high
        this.low = low
    }

    fun setMaxTone(maxTone: Int) {
        this.maxTone = maxTone
    }

    fun setMinTone(minTone: Int) {
        this.minTone = minTone
    }

    fun setEchoEnabled(echoEnabled: Boolean) {
        this.echoEnabled = echoEnabled
    }

    fun dispose() {
        timer?.cancel()
        soundPool?.release()
        soundPool = null
    }
}