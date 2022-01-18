package com.topview.purejoy.common.music.service.recover

import android.os.Handler
import com.topview.purejoy.common.IPCListenerController
import com.topview.purejoy.common.IPCModeController
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.impl.ipc.BinderPool
import com.topview.purejoy.common.music.player.impl.ipc.IPCDataControllerImpl
import com.topview.purejoy.common.music.player.impl.ipc.IPCListenerControllerImpl
import com.topview.purejoy.common.music.player.impl.ipc.IPCModeControllerImpl
import com.topview.purejoy.common.music.player.setting.MediaModeSetting
import com.topview.purejoy.common.music.player.util.castAs
import com.topview.purejoy.common.music.service.MusicService
import com.topview.purejoy.common.music.service.entity.*
import com.topview.purejoy.common.music.service.recover.db.RecoverDatabase
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverALData
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverARData
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverMusicData
import com.topview.purejoy.common.music.service.recover.db.initDB
import com.topview.purejoy.common.music.util.ExecutorInstance
import com.topview.purejoy.common.music.util.SP
import java.util.concurrent.CopyOnWriteArrayList

class RecoverTask(val dataController: IPCDataControllerImpl<MusicItem>,
                  val modeController: IPCModeControllerImpl,
                  val listenerController: IPCListenerControllerImpl,
                  val mainHandler: Handler,
                  val storeDuration: Long) : Runnable {
    private val rmPhoto = CopyOnWriteArrayList<RecoverMusicData>()
    private val rlPhoto = CopyOnWriteArrayList<RecoverALData>()
    private val rrPhoto = CopyOnWriteArrayList<RecoverARData>()


    private val recoverDB = "recover"

    private val db: RecoverDatabase by lazy {
        initDB(RecoverDatabase::class.java, recoverDB)
    }


    override fun run() {
        if (rmPhoto.size != dataController.mediaSource.size) {
            storePlayerState()
        } else {
            var changed = false

            for(item in dataController.mediaSource) {
                if (!rmPhoto.contains(item.toRecoverMusic())) {
                    changed = true
                    break
                }
            }

            if (changed) {
                storePlayerState()
            }
        }
        mainHandler.postDelayed(this, storeDuration)
    }

    private fun storeState(mode: Int, position: Int, max: Int) {
        SP.sp.edit().apply {
            putInt(MusicService.MODE_KEY, mode)
            putInt(MusicService.POSITION_KEY, position)
            putInt(MusicService.MAX_KEY, max)
            apply()
        }
    }

    fun storePlayerState() {
        val dao = db.recoverDao()
        ExecutorInstance.getInstance().execute {
            val tm = ArrayList(rmPhoto)
            val tl = ArrayList(rlPhoto)
            val tr = ArrayList(rrPhoto)
            dao.deleteRecoverData(tm, tl, tr)

            rmPhoto.clear()
            rrPhoto.clear()
            rlPhoto.clear()
            val list = dataController.mediaSource
            if (list.isNotEmpty()) {
                translateList(rmPhoto, rrPhoto, rlPhoto)
                tm.clear()
                tm.addAll(rmPhoto)
                tl.clear()
                tl.addAll(rlPhoto)
                tr.clear()
                tr.addAll(rrPhoto)

                dao.insertRecoverData(tm, tl, tr)

                modeController.let {
                    storeState(it.realController.current, dataController.position.current(), list.size)
                }

            } else {
                storeState(MediaModeSetting.ORDER, MediaModeSetting.INIT_POSITION, 0)
            }
        }
    }

    private fun translateList(recoverMusicList: MutableList<RecoverMusicData>,
                      recoverARList: MutableList<RecoverARData>,
                      recoverALList: MutableList<RecoverALData>) {
        val list = dataController.mediaSource
        for (l in list) {
            recoverMusicList.add(l.toRecoverMusic())
            for (a in l.ar) {
                recoverARList.add(a.toRecoverAR(l.id))
            }
            recoverALList.add(l.al.toRecoverAL(l.id))
        }
    }

    fun readLocal() {
        ExecutorInstance.getInstance().execute {
            val list = mutableListOf<MusicItem>()
            val dao = db.recoverDao()
            val data = dao.obtainRecoverData()
            val recoverMusic = mutableListOf<RecoverMusicData>()
            val recoverAl = mutableListOf<RecoverALData>()
            val recoverAr = mutableListOf<RecoverARData>()
            for (d in data) {
                list.add(d.toMusicItem())
                recoverMusic.add(d.musicData)
                recoverAl.add(d.alData)
                recoverAr.addAll(d.arDataList)
            }

            if (list.isNotEmpty()) {
                mainHandler.post {
                    rmPhoto.addAll(recoverMusic)
                    rlPhoto.addAll(recoverAl)
                    rrPhoto.addAll(recoverAr)
                    val source = mutableListOf<Wrapper>()
                    list.forEach {
                        source.add(it.wrap())
                    }
                    dataController.addAll(source)

                    mainHandler.post {
                        val current = dataController.current()
                        if (current != null) {
                            listenerController.invokeItemChangeListener(current)
                        }
                    }
                }

            }

        }
    }
}