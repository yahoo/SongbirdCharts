/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.audiofx.PresetReverb
import com.yahoo.mobile.android.songbird.util.AudioChartSettingsHelper
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule
import kotlin.experimental.and

class AudioChartAudioHelper(
    private val audioChartSettingsHelper: AudioChartSettingsHelper
) {

    companion object {
        const val TIMER_NAME = "Summary"
        const val TIMER_DELAY = 500L
        const val VOLTAGE = 32767
        const val SESSION_ID = 1
    }

    private lateinit var audioTrack: AudioTrack

    private var min = 0.0
    private var max = 0.0

    private var minFreq = audioChartSettingsHelper.getMinimumFrequency()
    private var maxFreq = audioChartSettingsHelper.getMaximumFrequency()

    private var timer: TimerTask? = null
    private val sampleRate = 4000
    private var duration = 1
    private var currentYValue = 0.0
    private var summaryIndex = 0
    private var echoEnabled = audioChartSettingsHelper.getEchoChecked()
    private val underPreviousCloseEffect: PresetReverb by lazy {
        PresetReverb(0,
            SESSION_ID
        ).apply {
            preset = PresetReverb.PRESET_LARGEHALL
        }
    }

    init {
        audioChartSettingsHelper.configurationUpdateListeners.add {
            when (it) {
                is AudioChartSettingsHelper.AccessibleConfiguration.MinimumFrequencyConfiguration -> {
                    minFreq = audioChartSettingsHelper.getMinimumFrequency()
                }
                is AudioChartSettingsHelper.AccessibleConfiguration.MaximumFrequencyConfiguration -> {
                    maxFreq = audioChartSettingsHelper.getMaximumFrequency()
                }
                is AudioChartSettingsHelper.AccessibleConfiguration.EchoConfiguration -> {
                    echoEnabled = audioChartSettingsHelper.getEchoChecked()
                }
            }
        }
    }

    private fun playToneAtGivenProgress(previousClose: Double, yValue: Double) {
        stopAllAudio()

        val numSamples = duration * sampleRate
        val generatedSound = ByteArray(2 * numSamples)

        val samplingRate = sampleRate / getFrequencyFromYValue(yValue)

        for (index in 0 until numSamples) {
            val newFrequency = (getNormalizedFrequency(index, samplingRate) * VOLTAGE).toShort()
            generatedSound[index] = (newFrequency and 0x00ff).toByte()
            generatedSound[index] = (newFrequency and (0xff00).toShort()).toInt().ushr(8).toByte()
        }

        audioTrack = AudioTrack(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build(),
            AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setSampleRate(sampleRate)
                .build(),
            2 * duration * sampleRate,
            AudioTrack.MODE_STATIC,
            SESSION_ID
        )

        audioTrack.write(generatedSound, 0, generatedSound.size)
        audioTrack.play()

        underPreviousCloseEffect.enabled = yValue < previousClose && echoEnabled
    }

    private fun getNormalizedFrequency(index: Int, samplingRate: Double) = Math.sin(2.0 * Math.PI * index.toDouble() / samplingRate)

    private fun stopAllAudio() {
        if (::audioTrack.isInitialized && audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause()
            audioTrack.flush()
            audioTrack.stop()
            audioTrack.release()
        }
    }

    private fun getFrequencyFromYValue(value: Double): Double {
        val percentage = (value - min) / (max - min)
        val freqValue = (maxFreq - minFreq) * percentage
        return freqValue + minFreq
    }

    fun playSummaryAudio(previousClose: Double, points: List<Double>) {
        timer?.cancel()
        summaryIndex = 0
        timer = Timer(TIMER_NAME, false).schedule(0, TIMER_DELAY) {
            if (summaryIndex < points.size) {
                playToneAtGivenProgress(previousClose, points[summaryIndex++])
            }
        }
    }

    fun onPointFocused(previousClose: Double, newY: Double) {
        if (currentYValue != newY) {
            playToneAtGivenProgress(previousClose, newY)
            currentYValue = newY
        }
    }

    fun updateMinMax(min: Double, max: Double) {
        this.min = min
        this.max = max
    }

    fun dispose() {
        timer?.cancel()
    }
}