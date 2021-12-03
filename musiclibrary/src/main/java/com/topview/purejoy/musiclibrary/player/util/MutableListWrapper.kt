package com.topview.purejoy.musiclibrary.player.util

/**
 * MutableList的封装类，为了方便插桩/过滤/实现观察者模式
 */
open class MutableListWrapper<T>(private val list: MutableList<T>,
) : MutableList<T> {
    override val size: Int
        get() = list.size

    override fun contains(element: T): Boolean {
        return list.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return list.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    override fun add(element: T): Boolean {
        beforeAdding(element)
        val b = if (isIntercepted(element)) {
            intercept(element)
            false
        } else {
            list.add(element)
        }
        if (b) {
            afterAdding(element)
        }
        return b
    }

    override fun addAll(elements: Collection<T>): Boolean {
        beforeAddAll(elements)
        val collect = mutableListOf<T>()
        elements.forEach {
            if (isIntercepted(it)) {
                intercept(it)
            } else {
                list.add(it)
                collect.add(it)
            }
        }
        afterAddAll(collect)
        return true
    }

    override fun clear() {
        beforeClearing()
        list.clear()
        afterClearing()
    }

    override fun iterator(): MutableIterator<T> {
        return list.listIterator()
    }

    override fun remove(element: T): Boolean {
        beforeRemoving(element)
        val index = list.indexOf(element)
        val b = list.remove(element)
        afterRemoving(element, index)
        return b
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        beforeRemovingAll(elements)
        val collect = mutableListOf<T>()
        elements.forEach {
            if (list.contains(it)) {
                list.remove(it)
                collect.add(it)
            }
        }
        afterRemovingAll(collect)
        return true
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return list.retainAll(elements)
    }

    open fun beforeRemovingAll(source: Collection<T>) {


    }

    open fun afterRemovingAll(change: MutableList<T>) {

    }

    open fun beforeClearing() {

    }

    open fun afterClearing() {

    }

    open fun isIntercepted(item: T) : Boolean {
        return contains(item)
    }

    open fun beforeAddAll(elements: Collection<T>) {

    }

    open fun afterAddAll(elements: MutableList<T>) {

    }

    open fun intercept(item: T) {

    }

    open fun beforeAdding(item: T) {

    }

    open fun beforeAdding(index: Int, item: T) {

    }

    open fun afterAdding(item: T) {

    }

    open fun beforeRemoving(item: T) {

    }

    open fun afterRemoving(item: T, index: Int) {

    }

    override fun get(index: Int): T {
        return list[index]
    }

    override fun indexOf(element: T): Int {
        return list.indexOf(element)
    }

    override fun lastIndexOf(element: T): Int {
        return list.lastIndexOf(element)
    }

    override fun add(index: Int, element: T) {
        beforeAdding(index, element)
        if (isIntercepted(element)) {
            intercept(element)
        } else {
            list.add(index, element)
        }
        afterAdding(element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        elements.forEach {
            if (isIntercepted(it)) {
                intercept(it)
            } else {
                list.add(index, it)
            }
        }
        return true
    }

    override fun listIterator(): MutableListIterator<T> {
        return list.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        return list.listIterator(index)
    }

    override fun removeAt(index: Int): T {
        val item = list[index]
        beforeRemoving(item)
        list.removeAt(index)
        afterRemoving(item, index)
        return item
    }

    override fun set(index: Int, element: T): T {
        list[index] = element
        return list[index]
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return list.subList(fromIndex, toIndex)
    }

}