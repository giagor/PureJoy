package com.topview.purejoy.common.component.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.topview.purejoy.common.R
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.component.webview.WebViewConstant.URL_EXTRA

open class CommonWebViewActivity : CommonActivity() {
    protected lateinit var webView: WebView
    protected lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariables()
        initViews()
        initSettings()
        initData()
    }

    override fun getLayoutId(): Int = R.layout.activity_common_webview

    protected fun initVariables() {
        check(intent.getStringExtra(URL_EXTRA) != null) { "url is not allowed to be null" }
        url = intent.getStringExtra(URL_EXTRA)!!
    }

    protected fun initViews() {
        webView = findViewById(R.id.common_wv)
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected fun initSettings() {
        val webSettings: WebSettings = webView.settings
        // 调整图片至适合webview的大小
        webSettings.useWideViewPort = true;
        // 缩放至屏幕的大小
        webSettings.loadWithOverviewMode = true;
        // 开启javascript
        webSettings.javaScriptEnabled = true
        // 网页在WebView中显示，而不是去打开系统浏览器
        webView.webViewClient = WebViewClient()
    }

    protected fun initData() {
        webView.loadUrl(url)
    }
}