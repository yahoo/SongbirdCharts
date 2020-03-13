/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.util

import java.math.RoundingMode
import java.text.NumberFormat

object ValueFormatter {

    @JvmStatic
    private fun createFormat(precision: Int): NumberFormat {
        return createFormat(precision, precision)
    }

    @JvmStatic
    private fun createFormat(
        minPrecision: Int,
        maxPrecision: Int
    ): NumberFormat {

        return NumberFormat.getInstance().apply {
            minimumFractionDigits = minPrecision
            maximumFractionDigits = maxPrecision
            roundingMode = RoundingMode.HALF_UP
        }
    }

    @JvmStatic
    fun getAsFormattedPrice(value: Double, priceHint: Double): String {
        if (value.isNaN() || value.isInfinite()) return ""

        val formatter: NumberFormat = if (!priceHint.isNaN() && priceHint > 2) {
            createFormat(priceHint.toInt())
        } else {
            createFormat(2)
        }

        return formatter.format(value)
    }
}
