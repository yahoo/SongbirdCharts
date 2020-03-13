/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
@file:JvmName("Extensions")

package com.yahoo.mobile.android.songbird.extensions

import android.content.Context
import android.util.DisplayMetrics

fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun Context.getDisplayMetrics(): DisplayMetrics = this.resources.displayMetrics