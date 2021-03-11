/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.util

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.RawRes
import com.yahoo.mobile.android.songbird.player.AudioPlayer

class SettingsHelper(
    context: Context
) {

    companion object {
        const val USER_PREF_KEY = "ACCESSIBLE_PREF_KEY"
        const val TALK_BACK_STRING_KEY = "TALKBACK_STRING_KEY"
        const val DATE_STRING_KEY = "DATE_STRING_KEY"
        const val MINIMUM_TONE_KEY = "MINIMUM_TONE_KEY"
        const val MAXIMUM_TONE_KEY = "MAXIMUM_TONE_KEY"
        const val ECHO_CHECKED_KEY = "ECHO_CHECKED_KEY"
    }

    private val minimumTone = 0
    private val maximumTone = AudioPlayer.tones.size-1

    enum class TalkBackOrder {
        X_Y,
        Y_X,
        Y;

        companion object {
            @JvmStatic
            fun asOrder(value: String?) = values().firstOrNull { it.name == value } ?: X_Y
        }
    }

    enum class DateOrder {
        TIME,
        MONTH_DAY_TIME,
        MONTH_DAY_YEAR_TIME;

        companion object {
            @JvmStatic
            fun asOrder(value: String?) = values().firstOrNull { it.name == value } ?: MONTH_DAY_YEAR_TIME
        }
    }

    val configurationUpdateListeners = mutableListOf<((AccessibleConfiguration) -> Unit)>()

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(USER_PREF_KEY, Context.MODE_PRIVATE)

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            DATE_STRING_KEY, TALK_BACK_STRING_KEY -> {
                configurationUpdateListeners.forEach {
                    it.invoke(
                        AccessibleConfiguration.TalkBackConfiguration()
                    )
                }
            }
            MINIMUM_TONE_KEY -> {
                configurationUpdateListeners.forEach {
                    it.invoke(
                        AccessibleConfiguration.MinimumFrequencyConfiguration()
                    )
                }
            }
            MAXIMUM_TONE_KEY -> {
                configurationUpdateListeners.forEach {
                    it.invoke(
                        AccessibleConfiguration.MaximumFrequencyConfiguration()
                    )
                }
            }
            ECHO_CHECKED_KEY -> {
                configurationUpdateListeners.forEach {
                    it.invoke(
                        AccessibleConfiguration.EchoConfiguration()
                    )
                }
            }
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    fun getTalkBackString(formattedPrice: String, timestampMs: Long): String {
        return when (getTalkBackOrder()) {
            TalkBackOrder.X_Y -> "${getDateString(timestampMs)} $formattedPrice"
            TalkBackOrder.Y_X -> "$formattedPrice ${getDateString(timestampMs)}"
            TalkBackOrder.Y -> formattedPrice
        }
    }

    private fun getDateString(timestampMs: Long): String {
        return when (getDateOrder()) {
            DateOrder.TIME -> DateTimeUtils.getTimeOnly(timestampMs)
            DateOrder.MONTH_DAY_TIME -> DateTimeUtils.getMonthDayTime(timestampMs)
            DateOrder.MONTH_DAY_YEAR_TIME -> DateTimeUtils.getMonthDayYearTime(timestampMs)
        }
    }

    private fun getTalkBackOrder(): TalkBackOrder =
        TalkBackOrder.asOrder(
            sharedPreferences.getString(
                TALK_BACK_STRING_KEY,
                ""
            )
        )

    fun getSavedOrderPosition(): Int = TalkBackOrder.values().indexOf(getTalkBackOrder())

    fun getSavedDateOrderPosition(): Int = DateOrder.values().indexOf(getDateOrder())

    private fun setTalkBackOrder(talkBackOrder: TalkBackOrder) {
        sharedPreferences.edit().putString(TALK_BACK_STRING_KEY, talkBackOrder.name).apply()
    }

    private fun setDateOrder(dateOrder: DateOrder) {
        sharedPreferences.edit().putString(DATE_STRING_KEY, dateOrder.name).apply()
    }

    private fun getDateOrder(): DateOrder =
        DateOrder.asOrder(
            sharedPreferences.getString(
                DATE_STRING_KEY,
                ""
            )
        )

    fun setOrderPosition(position: Int) {
        setTalkBackOrder(TalkBackOrder.values()[position])
    }

    fun setDatePosition(position: Int) {
        setDateOrder(DateOrder.values()[position])
    }

    fun getMinimumTone() = sharedPreferences.getInt(MINIMUM_TONE_KEY, minimumTone)

    fun setMinimumTone(@RawRes minTone: Int) {
        sharedPreferences.edit().putInt(MINIMUM_TONE_KEY, minTone).apply()
    }

    fun getMaximumTone() = sharedPreferences.getInt(MAXIMUM_TONE_KEY, maximumTone)

    fun setMaximumTone(@RawRes maxTone: Int) {
        sharedPreferences.edit().putInt(MAXIMUM_TONE_KEY, maxTone).apply()
    }

    fun getEchoChecked() = sharedPreferences.getBoolean(ECHO_CHECKED_KEY, true)

    fun switchEchoToggle() {
        sharedPreferences.edit().putBoolean(ECHO_CHECKED_KEY, !getEchoChecked()).apply()
    }

    sealed class AccessibleConfiguration {
        class TalkBackConfiguration : AccessibleConfiguration()
        class MinimumFrequencyConfiguration : AccessibleConfiguration()
        class MaximumFrequencyConfiguration : AccessibleConfiguration()
        class EchoConfiguration : AccessibleConfiguration()
    }
}
