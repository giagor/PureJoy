package com.topview.purejoy.musiclibrary.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class DataBindingAdapter<T : DataBindingAdapter.BindingHolder> : RecyclerView.Adapter<T>() {

    open class BindingHolder(val viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        open fun bind(variableId: Int, data: Any) {
            viewBinding.setVariable(variableId, data)
            viewBinding.executePendingBindings()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        val viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context), viewType, parent, false)
        return createHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        holder.bind(variableId(getItemViewType(position)), getItem(position))
    }

    abstract fun variableId(viewType: Int): Int

    abstract fun getItem(position: Int): Any

    abstract fun createHolder(viewBinding: ViewDataBinding): T


}