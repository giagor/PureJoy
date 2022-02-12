package com.topview.purejoy.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

/**
 * 便于Compose使用的Fragment
 */
abstract class ComposeFragment: Fragment() {
    /**
     * ComposeView使用的LayoutParams
     */
    open val composeViewLayoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beforeComposeViewCreate()
        val view = ComposeView(inflater.context).apply {
            beforeSetContent()
            layoutParams = composeViewLayoutParams
            setContent {
                FragmentContent()
            }
            afterSetContent()
        }
        afterComposeViewCreate()
        return view
    }

    @Composable
    protected abstract fun ComposeView.FragmentContent()

    /**
     * 在ComposeView被创建前会被调用
     */
    protected open fun beforeComposeViewCreate() {}

    /**
     * 为ComposeView设置好Composition前被调用
     */
    protected open fun ComposeView.beforeSetContent() {}

    /**
     * 为ComposeView设置好Composition后被调用
     */
    protected open fun ComposeView.afterSetContent() {}

    /**
     * 创建ComposeView后会被调用
     */
    protected open fun afterComposeViewCreate() {}
}

