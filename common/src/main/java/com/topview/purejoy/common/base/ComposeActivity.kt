package com.topview.purejoy.common.base

abstract class ComposeActivity: CommonActivity() {
    override fun getLayoutId() = 0
    override fun setContentView() {}
}