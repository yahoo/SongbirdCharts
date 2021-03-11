/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Handles touch and scrub events to the chart
 * Necessary for knowing where on the chart the user is
 * and playing the tone accordingly
 */
class ScrubRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var onScrubListener: ScrubCallback? = null

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                onScrubListener?.onScrub(e.rawX)
            }
            MotionEvent.ACTION_UP -> {
                onScrubListener?.onRelease(e.rawX)
            }
        }
        return super.onTouchEvent(e)
    }

    interface ScrubCallback {
        fun onScrub(x: Float)
        fun onRelease(x: Float)
    }
}