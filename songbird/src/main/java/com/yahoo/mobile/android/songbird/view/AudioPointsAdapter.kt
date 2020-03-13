/**
 * Copyright 2020, Verizon Media
 * Licensed under the terms of the Apache 2.0 license. See LICENSE file in project root for terms.
 */
package com.yahoo.mobile.android.songbird.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yahoo.mobile.android.songbird.R
import com.yahoo.mobile.android.songbird.model.AudioChartPointViewModel

class AudioPointsAdapter : RecyclerView.Adapter<AudioPointsAdapter.ViewHolder>() {

    var focusedIndex: Int = -1
    lateinit var items: List<AudioChartPointViewModel>

    override fun getItemViewType(position: Int) = items[position].resId

    override fun getItemCount() = if (::items.isInitialized) items.size else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            val point: View = holder.itemView.findViewById(R.id.point)
            items[position].bind(point)
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        if (focusedIndex != -1) {
            holder.itemView.apply {
                if (focusedIndex == holder.adapterPosition) {
                    requestFocus()
                    focusedIndex = -1
                }
            }
        } else {
            holder.itemView.apply {
                if (isFocused) {
                    clearFocus()
                }
            }
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}