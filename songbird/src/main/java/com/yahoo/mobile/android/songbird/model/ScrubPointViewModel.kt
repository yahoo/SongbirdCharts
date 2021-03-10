/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.model

import android.os.Build
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.LayoutRes
import com.yahoo.mobile.android.songbird.R

class ScrubPointViewModel(
    val value: Double,
    val timestamp: Long,
    val index: Int,
    var heading: String = "",
    var talkbackString: String,
    val onSwipeListener: (() -> Unit)
) {

    @LayoutRes
    val resId: Int = R.layout.list_item_scrub_point

    fun bind(point: View) {
        point.apply {
            setAccessibilityDelegate(
                object : View.AccessibilityDelegate() {
                    override fun dispatchPopulateAccessibilityEvent(
                        host: View,
                        event: AccessibilityEvent
                    ): Boolean {
                        if (event.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                            onSwipeListener()
                        }
                        return super.dispatchPopulateAccessibilityEvent(host, event)
                    }
                }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                isAccessibilityHeading = heading.isNotEmpty()
            }
            contentDescription = "$heading $talkbackString"
        }
    }
}
