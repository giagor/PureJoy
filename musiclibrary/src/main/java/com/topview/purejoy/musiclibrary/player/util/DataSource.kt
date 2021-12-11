package com.topview.purejoy.musiclibrary.player.util

open class DataSource<T>(
    list: MutableList<T> = mutableListOf(),
    val changeListeners: MutableList<DataSetChangeListener<T>> = mutableListOf(),
    val removeListeners: MutableList<RemoveListener<T>> = mutableListOf()
) : MutableListWrapper<T>(list) {
    override fun afterClearing() {
        invokeDataSetChangeListeners(mutableListOf())
    }

    override fun afterAddAll(elements: MutableList<T>) {
        if (elements.isNotEmpty()) {
            invokeDataSetChangeListeners(elements)
        }
    }

    override fun afterAdding(item: T) {
        invokeDataSetChangeListeners(mutableListOf(item))
    }

    override fun afterRemoving(item: T, index: Int) {
        if (index != -1) {
            invokeRemoveListeners(item, index)
            invokeDataSetChangeListeners(mutableListOf(item))
        }
    }

    override fun afterRemovingAll(change: MutableList<T>) {
        if (change.isNotEmpty()) {
            invokeDataSetChangeListeners(change)
        }
    }

    private fun invokeDataSetChangeListeners(changes: MutableList<T>) {
        changeListeners.forEach {
            it.onChange(changes)
        }
    }

    private fun invokeRemoveListeners(item: T, index: Int) {
        removeListeners.forEach {
            it.onRemoved(item, index)
        }
    }

    interface DataSetChangeListener<T> {
        /**
         * @param changes 数据集的变化
         * 如果changes为空集合则说明数据集被清空，否则则是数据集变化的数据
         */
        fun onChange(changes: MutableList<T>)
    }

    interface RemoveListener<T> {
        /**
         * @param element 从数据集中移除的数据
         * @param index 移除的数据原本位于数据集中的位置
         */
        fun onRemoved(element: T, index: Int)
    }




}