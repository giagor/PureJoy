package com.topview.purejoy.common.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ThreadUtil {
    private val IOScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    fun runOnIO(block: () -> Unit) {
        IOScope.launch {
            block.invoke()
        }
    }
}