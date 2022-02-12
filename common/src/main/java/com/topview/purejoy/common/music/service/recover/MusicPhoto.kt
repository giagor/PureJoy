package com.topview.purejoy.common.music.service.recover

import android.os.Handler
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.impl.ipc.IPCDataControllerImpl
import com.topview.purejoy.common.music.player.impl.ipc.IPCListenerControllerImpl
import com.topview.purejoy.common.music.service.entity.*
import com.topview.purejoy.common.music.service.recover.db.RecoverDatabase
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverALData
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverARData
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverMusicData
import com.topview.purejoy.common.music.service.recover.db.initDB
import com.topview.purejoy.common.music.util.ExecutorInstance
import java.util.concurrent.CopyOnWriteArrayList

class MusicPhoto(val dataController: IPCDataControllerImpl<MusicItem>,
                 val mainHandler: Handler,
                 val listenerController: IPCListenerControllerImpl, ) {
    private val rmPhoto = CopyOnWriteArrayList<RecoverMusicData>()
    private val rlPhoto = CopyOnWriteArrayList<RecoverALData>()
    private val rrPhoto = CopyOnWriteArrayList<RecoverARData>()
    private val recoverDB = "recover"

    private val db: RecoverDatabase by lazy {
        initDB(RecoverDatabase::class.java, recoverDB)
    }

    fun updatePhoto() {
        ExecutorInstance.getInstance().execute {
            val dao = db.recoverDao()
            val s = ArrayList(dataController.mediaSource)
            val ids = mutableSetOf<Long>()
            val am = mutableListOf<RecoverMusicData>()
            val al = mutableListOf<RecoverALData>()
            val ar = mutableListOf<RecoverARData>()
            for (d in s) {
                ids.add(d.id)
                val m = d.toRecoverMusic()
                val l = d.al.toRecoverAL(d.id)
                val rl = mutableListOf<RecoverARData>()
                for (r in d.ar) {
                    rl.add(r.toRecoverAR(d.id))
                }
                if (!rmPhoto.contains(m)) {
                    am.add(m)
                    al.add(l)
                    ar.addAll(rl)
                }
            }
            if (am.isNotEmpty()) {
                dao.insertRecoverData(am, al, ar)
                rmPhoto.addAll(am.toSet())
                rlPhoto.addAll(al.toSet())
                rrPhoto.addAll(ar.toSet())
            }
            val rm = mutableListOf<RecoverMusicData>()
            val rl = mutableListOf<RecoverALData>()
            val rr = mutableListOf<RecoverARData>()
            rmPhoto.forEach {
                if (!ids.contains(it.id)) {
                    rm.add(it)
                }
            }
            rlPhoto.forEach {
                if (!ids.contains(it.mid)) {
                    rl.add(it)
                }
            }
            rrPhoto.forEach {
                if (!ids.contains(it.mid)) {
                    rr.add(it)
                }
            }
            if (rm.isNotEmpty()) {
                dao.deleteRecoverData(rm, rl, rr)
                rmPhoto.removeAll(rm.toSet())
                rlPhoto.removeAll(rl.toSet())
                rrPhoto.removeAll(rr.toSet())
            }
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