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
import kotlin.math.max

class AudioPlayer(
    context: Context
) {

    companion object {
        const val TIMER_NAME = "Summary"
        const val TIMER_DELAY = 250L
        val tones = intArrayOf(
            R.raw.c1sharp_no_reverb,
            R.raw.d1sharp_no_reverb,
            R.raw.f1sharp_no_reverb,
            R.raw.g1sharp_no_reverb,
            R.raw.a1sharp_no_reverb,
            R.raw.c2sharp_no_reverb,
            R.raw.d2sharp_no_reverb,
            R.raw.f2sharp_no_reverb,
            R.raw.g2sharp_no_reverb,
            R.raw.a2sharp_no_reverb,
            R.raw.c3sharp_no_reverb,
            R.raw.d3sharp_no_reverb,
            R.raw.f3sharp_no_reverb,
            R.raw.g3sharp_no_reverb,
            R.raw.a3sharp_no_reverb,
            R.raw.c4sharp_no_reverb,
            R.raw.d4sharp_no_reverb,
            R.raw.f4sharp_no_reverb,
            R.raw.g4sharp_no_reverb,
            R.raw.a4sharp_no_reverb,
            R.raw.c5sharp_no_reverb,
            R.raw.d5sharp_no_reverb,
            R.raw.f5sharp_no_reverb,
            R.raw.g5sharp_no_reverb,
            R.raw.a5sharp_no_reverb
        )
    }

    private val tonesReverb = intArrayOf(
        R.raw.c1sharp_reverb,
        R.raw.d1sharp_reverb,
        R.raw.f1sharp_reverb,
        R.raw.g1sharp_reverb,
        R.raw.a1sharp_reverb,
        R.raw.c2sharp_reverb,
        R.raw.d2sharp_reverb,
        R.raw.f2sharp_reverb,
        R.raw.g2sharp_reverb,
        R.raw.a2sharp_reverb,
        R.raw.c3sharp_reverb,
        R.raw.d3sharp_reverb,
        R.raw.f3sharp_reverb,
        R.raw.g3sharp_reverb,
        R.raw.a3sharp_reverb,
        R.raw.c4sharp_reverb,
        R.raw.d4sharp_reverb,
        R.raw.f4sharp_reverb,
        R.raw.g4sharp_reverb,
        R.raw.a4sharp_reverb,
        R.raw.c5sharp_reverb,
        R.raw.d5sharp_reverb,
        R.raw.f5sharp_reverb,
        R.raw.g5sharp_reverb,
        R.raw.a5sharp_reverb
    )
    private var high = 0.0
    private var low = 0.0
    private var maxTone = tones.size-1
    private var minTone = 0
    private var echoEnabled = true

    private var timer: TimerTask? = null
    private var currentY = 0.0
    private val sounds = mutableListOf<Int>()
    private val soundsReverb = mutableListOf<Int>()
    private var soundPool: SoundPool? = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    private var soundPoolReverb: SoundPool? = SoundPool.Builder()
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
            sounds.add(
                soundPool?.load(context, tone, 0) ?: 0
            )
        }
        for (tone in tonesReverb) {
            soundsReverb.add(
                soundPoolReverb?.load(context, tone, 0) ?: 0
            )
        }
    }

    private fun playToneAtGivenProgress(benchmark: Double, y: Double) {
        if (y < benchmark && echoEnabled) {
            soundPoolReverb?.play(soundsReverb[getIndexFromY(y)], 1f, 1f, 0, 0, 1f)
        } else {
            soundPool?.play(sounds[getIndexFromY(y)], 1f, 1f, 0, 0, 1f)
        }
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