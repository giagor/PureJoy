package com.topview.purejoy.common.music.util

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

class ExecutorInstance private constructor() {

    val service: ExecutorService

    init {
        val core = Runtime.getRuntime().availableProcessors()
        service = ThreadPoolExecutor(core, core, 0, TimeUnit.MILLISECONDS, LinkedBlockingQueue(), DaemonFactory())
    }


    fun execute(runnable: Runnable) {
        service.execute {
            runnable.run()
        }
    }

    fun <T> submit(callable: Callable<T>): Future<T> {
        return service.submit(callable)
    }

    fun shutdown() {
        service.shutdown()
    }

    private class DaemonFactory : ThreadFactory {
        private val atomicInteger = AtomicInteger(1)
        private val factoryName = "DaemonFactory"

        override fun newThread(r: Runnable?): Thread {
            val t = Thread(r, "$factoryName-thread-${atomicInteger.getAndIncrement()}")
            t.isDaemon = true
            return t
        }

    }




    companion object {
        @Volatile
        private var instance: ExecutorInstance? = null


        fun getInstance(): ExecutorInstance {
            if (instance == null) {
                synchronized(ExecutorInstance::class.java) {
                    if (instance == null) {
                        instance = ExecutorInstance()
                    }
                }
            }
            return instance!!
        }
    }


}