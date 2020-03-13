/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.util

import android.content.Context
import android.content.SharedPreferences

class AudioChartSettingsHelper(
    context: Context
) {

    companion object {
        const val USER_PREF_KEY = "ACCESSIBLE_PREF_KEY"
        const val TALK_BACK_STRING_KEY = "TALKBACK_STRING_KEY"
        const val DATE_STRING_KEY = "DATE_STRING_KEY"
        const val MINIMUM_FREQUENCY_KEY = "MINIMUM_FREQUENCY_KEY"
        const val MAXIMUM_FREQUENCY_KEY = "MAXIMUM_FREQUENCY_KEY"
        const val ECHO_CHECKED_KEY = "ECHO_CHECKED_KEY"
        const val MINIMUM_FREQUENCY = 200
        const val MAXIMUM_FREQUENCY = 850
    }

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
            MINIMUM_FREQUENCY_KEY -> {
                configurationUpdateListeners.forEach {
                    it.invoke(
                        AccessibleConfiguration.MinimumFrequencyConfiguration()
                    )
                }
            }
            MAXIMUM_FREQUENCY_KEY -> {
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

    fun getMinimumFrequency() = sharedPreferences.getInt(
        MINIMUM_FREQUENCY_KEY,
        MINIMUM_FREQUENCY
    )

    fun setMinimumFrequency(minFrequency: Int) {
        if (minFrequency in MINIMUM_FREQUENCY until MAXIMUM_FREQUENCY) {
            sharedPreferences.edit().putInt(MINIMUM_FREQUENCY_KEY, minFrequency).apply()
        }
    }

    fun getMaximumFrequency() = sharedPreferences.getInt(
        MAXIMUM_FREQUENCY_KEY,
        MAXIMUM_FREQUENCY
    )

    fun setMaximumFrequency(maxFrequency: Int) {
        if (maxFrequency in (MINIMUM_FREQUENCY + 1)..MAXIMUM_FREQUENCY) {
            sharedPreferences.edit().putInt(MAXIMUM_FREQUENCY_KEY, maxFrequency).apply()
        }
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
