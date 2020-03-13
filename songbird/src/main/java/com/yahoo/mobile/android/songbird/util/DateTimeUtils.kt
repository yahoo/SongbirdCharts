/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.util

import android.annotation.SuppressLint
import android.text.format.DateFormat
import com.yahoo.mobile.android.songbird.extensions.unsafeLazy

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("ConstantLocale")
object DateTimeUtils {
    private const val LOCALE_REGION_HK = "HK"

    private val shortJustTime by unsafeLazy {
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "h:mm"), Locale.getDefault())
    }

    private val monthTime by unsafeLazy {
        SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM d h:mm"), Locale.getDefault())
    }

    private val formatMDY by unsafeLazy {
        if (LOCALE_REGION_HK.equals(Locale.getDefault().country, ignoreCase = true)) {
            SimpleDateFormat("yyyy/MM/dd h:mm", Locale.getDefault())
        } else {
            SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MM/dd/yyyy h:mm"), Locale.getDefault())
        }
    }

    @JvmStatic
    fun getTimeOnly(milliseconds: Long): String {
        return shortJustTime.format(Date(milliseconds))
    }

    @JvmStatic
    fun getMonthDayTime(milliseconds: Long): String {
        return monthTime.format(Date(milliseconds))
    }

    @JvmStatic
    fun getMonthDayYearTime(milliseconds: Long): String {
        return formatMDY.format(Date(milliseconds))
    }
}
