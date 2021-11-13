package com.topview.purejoy.musiclibrary.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class CommonAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(layoutId(), parent, false)
        return createViewHolder(view)
    }

    abstract fun layoutId(): Int

    abstract fun createViewHolder(root: View): VH
}