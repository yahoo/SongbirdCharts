/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
@file:JvmName("ResourceExtensions")

package com.yahoo.mobile.android.songbird.extensions

import android.content.res.Resources
import android.util.DisplayMetrics

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into
 * pixels
 * @return A float value to represent dp equivalent to px value
 */
fun Resources.dpToPx(dp: Int): Int {
    val metrics = displayMetrics
    return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}