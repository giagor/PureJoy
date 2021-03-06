package com.topview.purejoy.common.component.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.R
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.component.webview.WebViewConstant.URL_EXTRA
import com.topview.purejoy.common.router.CommonRouter

@Route(path = CommonRouter.ACTIVITY_COMMON_WEBVIEW)
open class CommonWebViewActivity : CommonActivity() {
    protected lateinit var webView: WebView
    protected lateinit var progressBar: ProgressBar
    protected lateinit var ivBackArrow: ImageView
    protected lateinit var tvTitle: TextView
    protected lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariables()
        initViews()
        initEvent()
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
        ivBackArrow = findViewById(R.id.common_iv_back_arrow)
        tvTitle = findViewById(R.id.common_tv_title)
        progressBar = findViewById(R.id.common_pb_loading_progress)
    }

    protected fun initEvent() {
        ivBackArrow.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected fun initSettings() {
        val webSettings: WebSettings = webView.settings
        // ?????????????????????webview?????????
        webSettings.useWideViewPort = true;
        // ????????????????????????
        webSettings.loadWithOverviewMode = true;
        // ??????javascript
        webSettings.javaScriptEnabled = true
        // ?????????WebView?????????????????????????????????????????????
        webView.webViewClient = WebViewClient()
        // ??????WebChromeClient
        webView.webChromeClient = CustomWebChromeClient()
    }

    protected fun initData() {
        webView.loadUrl(url)
    }

    private inner class CustomWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == 100) {
                progressBar.visibility = View.GONE
            } else {
                if (progressBar.visibility != View.VISIBLE) {
                    progressBar.visibility = View.VISIBLE
                }
                progressBar.progress = newProgress
            }
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            title?.let {
                tvTitle.text = it
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}