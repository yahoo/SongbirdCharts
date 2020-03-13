/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.util

import android.content.Context
import com.yahoo.mobile.android.songbird.extensions.getDisplayMetrics

object DimensionUtils {
    private const val DEFAULT_ITEM_WIDTH = 6

    fun getItemIndexByWidth(context: Context, numItems: Int, x: Float): Int {
        val itemWidth = context.getDisplayMetrics().widthPixels / (if (numItems == 0) DEFAULT_ITEM_WIDTH else numItems)
        return (x / itemWidth).toInt()
    }
}