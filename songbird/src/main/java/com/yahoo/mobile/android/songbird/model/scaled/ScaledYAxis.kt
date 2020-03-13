/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.model.scaled

class ScaledYAxis(
    val priceHint: Double,
    val previousClose: Double,
    val previousCloseX: Float,
    val previousCloseY: Float,
    val currentPrice: Double,
    val currentPriceX: Float,
    val currentPriceY: Float,
    val startX: Float,
    val topY: Float,
    val endX: Float,
    val bottomY: Float,
    val drawHighestPrice: Boolean,
    val drawLowestPrice: Boolean,
    val highestPrice: Double,
    val highestPriceX: Float,
    val highestPriceY: Float,
    val lowestPrice: Double,
    val lowestPriceX: Float,
    val lowestPriceY: Float
)