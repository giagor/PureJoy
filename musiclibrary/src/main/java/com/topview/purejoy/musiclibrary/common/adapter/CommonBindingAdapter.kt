package com.topview.purejoy.musiclibrary.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

abstract class CommonBindingAdapter<T, VH : CommonBindingAdapter.BindingHolder>
    (layoutId: Int, data: MutableList<T>? = null) : BaseQuickAdapter<T, VH>(layoutId, data) {

    abstract class BindingHolder(val viewDataBinding: ViewDataBinding) : BaseViewHolder(viewDataBinding.root) {
        open fun bind(data: Any?) {
            viewDataBinding.setVariable(variableId(), data)
            viewDataBinding.executePendingBindings()
        }

        abstract fun variableId(): Int
    }

    override fun convert(holder: VH, item: T) {
        holder.bind(item)
    }

    override fun createBaseViewHolder(parent: ViewGroup, layoutResId: Int): VH {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutResId, parent, false)
        return createBindingHolder(parent, layoutResId, binding)
    }

    abstract fun createBindingHolder(parent: ViewGroup, layoutResId: Int, binding: ViewDataBinding): VH
}