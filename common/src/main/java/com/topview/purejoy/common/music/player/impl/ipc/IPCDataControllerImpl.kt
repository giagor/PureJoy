package com.topview.purejoy.common.music.player.impl.ipc

import android.os.Handler
import android.os.Looper
import com.topview.purejoy.common.IPCDataController
import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.abs.core.Position
import com.topview.purejoy.common.music.player.abs.transformation.ItemTransformation
import com.topview.purejoy.common.music.player.util.DataSource
import com.topview.purejoy.common.music.player.util.checkWrapper
import com.topview.purejoy.common.music.player.util.ensureSecurity

open class IPCDataControllerImpl<T : Item>(
    val handler: Handler = Handler(Looper.getMainLooper()),
    val source: DataSource<Wrapper>,
    val mediaSource: DataSource<T>,
    var addCallback: ((Int, Wrapper?) -> Unit)? = null,
    var position: Position,
    var transformation: ItemTransformation<T>
) : IPCDataController.Stub() {
    override fun add(wrapper: Wrapper?) {
        handler.post {
            checkWrapper(wrapper) { w ->
                val v = transformation.transform(w)
                v?.let {
                    if (mediaSource.add(it)) {
                        if (source.add(w)) {
                            addCallback?.invoke(SUCCESS_CODE, wrapper)
                        } else {
                            mediaSource.removeAt(mediaSource.size - 1)
                            addCallback?.invoke(FAIL_CODE, wrapper)
                        }
                    } else {
                        addCallback?.invoke(FAIL_CODE, wrapper)
                    }
                }
                if (v == null) {
                    addCallback?.invoke(FAIL_CODE, wrapper)
                }
            }
        }
    }

    override fun addAfter(wrapper: Wrapper?, index: Int) {
        handler.post {
            ensureSecurity(source, index) {
                checkWrapper(wrapper) { w ->
                    val v = transformation.transform(w)
                    v?.let {
                        if (mediaSource.contains(it) || source.contains(wrapper)) {
                            addCallback?.invoke(FAIL_CODE, wrapper)
                        } else {
                            mediaSource.add(index + 1, it)
                            source.add(index + 1, w)
                            addCallback?.invoke(SUCCESS_CODE, wrapper)
                        }
                    }
                    if (v == null) {
                        addCallback?.invoke(FAIL_CODE, wrapper)
                    }
                }

            }
        }
    }


    override fun remove(wrapper: Wrapper?) {
        handler.post {
            checkWrapper(wrapper) { w ->
                val v = transformation.transform(w)
                if (mediaSource.remove(v)) {
                    source.remove(w)
                }
            }
        }
    }

    override fun addAll(wrapper: MutableList<Wrapper>?) {
        handler.post {
            wrapper?.let {
                val wl = mutableListOf<Wrapper>()
                val il = mutableListOf<T>()
                it.forEach { w ->
                    checkWrapper(w) { ww ->
                        val v = transformation.transform(ww)
                        v?.let { value ->
                            if (!mediaSource.isIntercepted(value) && !source.isIntercepted(ww)) {
                                wl.add(ww)
                                il.add(value)
                            }
                        }
                    }
                }
                mediaSource.addAll(il)
                source.addAll(wl)
            }
        }
    }

    override fun clear() {
        handler.post {
            mediaSource.clear()
            source.clear()
        }
    }

    override fun allItems(): MutableList<Wrapper> {
        return ArrayList(source)
    }

    override fun current(): Wrapper? {
        val c = position.current()
        return if (source.isEmpty() || c < 0 || c >= source.size) {
            null
        } else {
            source[c]
        }
    }



    companion object {
        const val FAIL_CODE = 0
        const val SUCCESS_CODE = 1
    }
}