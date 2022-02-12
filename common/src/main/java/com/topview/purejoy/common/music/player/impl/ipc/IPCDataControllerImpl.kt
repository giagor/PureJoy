package com.topview.purejoy.common.music.player.impl.ipc

import android.os.Handler
import android.os.Looper
import com.topview.purejoy.common.IPCDataController
import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.abs.core.Position
import com.topview.purejoy.common.music.player.abs.transformation.ItemTransformation
import com.topview.purejoy.common.music.player.impl.DataInterceptor
import com.topview.purejoy.common.music.player.impl.Operator
import com.topview.purejoy.common.music.player.impl.OperatorCallback
import com.topview.purejoy.common.music.player.impl.OperatorCallback.Companion.FAIL_CODE
import com.topview.purejoy.common.music.player.impl.OperatorCallback.Companion.SUCCESS_CODE
import com.topview.purejoy.common.music.player.util.DataSource
import com.topview.purejoy.common.music.player.util.checkWrapper
import com.topview.purejoy.common.music.player.util.ensureSecurity
import java.util.*
import kotlin.collections.ArrayList

open class IPCDataControllerImpl<T : Item>(
    val handler: Handler = Handler(Looper.getMainLooper()),
    val source: DataSource<Wrapper>,
    val mediaSource: DataSource<T>,
    var position: Position,
    var transformation: ItemTransformation<T>,
    var operatorCallback: OperatorCallback? = null,
    val interceptor: DataInterceptor<T>? = DefaultInterceptor()
) : IPCDataController.Stub() {
    override fun add(wrapper: Wrapper?) {
        handler.post {
            checkWrapper(wrapper) { w ->
                val v = transformation.transform(w)
                v?.let {
                    checkItem(w, v, {
                        mediaSource.add(v)
                        source.add(w)
                        operatorCallback?.callback(Operator.ADD, SUCCESS_CODE, w, null)
                    }, {
                        operatorCallback?.callback(Operator.ADD, FAIL_CODE, null, w)
                    })
                }
                if (v == null) {
                    operatorCallback?.callback(Operator.ADD, FAIL_CODE, null, w)
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
                        checkItem(w, v, {
                            mediaSource.add(index + 1, it)
                            source.add(index + 1, w)
                            operatorCallback?.callback(Operator.ADD, SUCCESS_CODE, w, null)
                        }, {
                            operatorCallback?.callback(Operator.ADD, FAIL_CODE, null, w)
                        })
                    }
                    if (v == null) {
                        operatorCallback?.callback(Operator.ADD, FAIL_CODE, null, w)
                    }
                }

            }
        }
    }


    override fun remove(wrapper: Wrapper?) {
        handler.post {
            checkWrapper(wrapper) { w ->
                val v = transformation.transform(w)
                if (mediaSource.remove(v) && source.remove(w)) {
                    operatorCallback?.callback(Operator.REMOVE, SUCCESS_CODE, w, null)
                } else {
                    operatorCallback?.callback(Operator.REMOVE, FAIL_CODE, null, w)
                }
            }
        }
    }

    override fun addAll(wrapper: MutableList<Wrapper>?) {
        handler.post {
            wrapper?.let {
                val wl = mutableListOf<Wrapper>()
                val fl = mutableListOf<Wrapper>()
                val il = mutableListOf<T>()
                it.forEach { w ->
                    checkWrapper(w) { ww ->
                        val v = transformation.transform(ww)
                        v?.let { value ->
                            checkItem(ww, v, {
                                wl.add(ww)
                                il.add(value)
                            }, {
                                fl.add(ww)
                            })
                        }
                    }
                }
                mediaSource.addAll(il)
                source.addAll(wl)
                val code = if (wl.size == it.size) {
                    SUCCESS_CODE
                } else {
                    FAIL_CODE
                }
                operatorCallback?.callback(Operator.ADD_ALL, code, wl, fl)
            }
            if (wrapper == null) {
                operatorCallback?.callback(Operator.ADD_ALL, FAIL_CODE, null, null)
            }
        }
    }

    override fun clear() {
        handler.post {
            val cl = ArrayList(source)
            mediaSource.clear()
            source.clear()
            operatorCallback?.callback(Operator.CLEAR, SUCCESS_CODE, cl, null)
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

    private fun checkItem(wrapper: Wrapper, item: T, success: () -> Unit, fail: () -> Unit) {
        val p = Collections.unmodifiableList(mediaSource)
        val pw = Collections.unmodifiableList(source)
        if (interceptor?.isIntercepted(wrapper, pw, p, item) == true) {
            interceptor.intercept(wrapper, item)
            fail.invoke()
        } else {
            success.invoke()
        }
    }


    private class DefaultInterceptor<T : Item> : DataInterceptor<T> {


        override fun intercept(wrapper: Wrapper, item: T) {

        }

        override fun isIntercepted(
            wrapper: Wrapper,
            wrappers: List<Wrapper>,
            source: List<T>,
            item: T
        ): Boolean {
            return wrappers.contains(wrapper) || source.contains(item)
        }

    }



}