package com.topview.purejoy.musiclibrary.common.util

import android.view.View
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.util.StatusBarUtil
import com.topview.purejoy.common.util.StatusBarUtil.setAutoFitSystemWindows

fun CommonActivity.becomeImmersive(root: View) {
    window.setAutoFitSystemWindows(false)
    StatusBarUtil.fitSystemBar(root)
}