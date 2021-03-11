package com.yahoo.mobile.android.songbird.player

import org.hamcrest.CoreMatchers.`is` as _is
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class AudioPlayerTest {

    companion object {
        const val HIGH = 100.0
        const val LOW = 13.0
    }

    @Test
    fun givenZeroY_getIndexFromY_returnZero() {
        val chartAudioPlayer = AudioPlayer()
        chartAudioPlayer.updateLowHighPoints(LOW, HIGH)
        assertThat(chartAudioPlayer.getIndexFromY(LOW), _is(0))
    }

    @Test
    fun givenMaxY_getIndexFromY_returnLastIndex() {
        val chartAudioPlayer = AudioPlayer()
        chartAudioPlayer.updateLowHighPoints(LOW, HIGH)
        assertThat(chartAudioPlayer.getIndexFromY(HIGH), _is(chartAudioPlayer.tones.size-1))
    }

    @Test
    fun givenFiftyY_getIndexFromY_returnMiddleIndex() {
        val chartAudioPlayer = AudioPlayer()
        chartAudioPlayer.updateLowHighPoints(LOW, HIGH)
        assertThat(chartAudioPlayer.getIndexFromY((HIGH-LOW) / 2 + LOW), _is((chartAudioPlayer.tones.size-1)/2))
    }
}