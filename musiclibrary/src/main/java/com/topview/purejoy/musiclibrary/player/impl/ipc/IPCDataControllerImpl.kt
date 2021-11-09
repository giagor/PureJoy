package com.topview.purejoy.musiclibrary.player.impl.ipc

import android.os.Handler
import android.os.Looper
import com.topview.purejoy.musiclibrary.IPCDataController
import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.player.abs.core.Position
import com.topview.purejoy.musiclibrary.player.util.DataSource
import com.topview.purejoy.musiclibrary.player.util.cast

open class IPCDataControllerImpl<T : Item>(
    val handler: Handler = Handler(Looper.getMainLooper()),
    val source: DataSource<Wrapper>,
    val mediaSource: DataSource<T>,
    var addCallback: ((Int, Wrapper?) -> Unit)? = null,
    var position: Position
) : IPCDataController.Stub() {
    override fun add(wrapper: Wrapper?) {
        handler.post {
            val v = wrapper?.value?.cast<T>()
            v?.let {
                if (mediaSource.add(it)) {
                    if (source.add(wrapper)) {
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

    override fun remove(wrapper: Wrapper?) {
        handler.post {
            wrapper?.value?.let {
                if (mediaSource.remove(it.cast())) {
                    source.remove(wrapper)
                }
            }
        }
    }

    override fun addAll(wrapper: MutableList<Wrapper>?) {
        handler.post {
            wrapper?.let {
                    list ->
                list.forEach {
                        w ->
                    w.value?.cast<T> {
                        if (mediaSource.add(it)) {
                            if (!source.add(w)) {
                                mediaSource.removeAt(mediaSource.size - 1)
                            }
                        }
                    }
                }
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