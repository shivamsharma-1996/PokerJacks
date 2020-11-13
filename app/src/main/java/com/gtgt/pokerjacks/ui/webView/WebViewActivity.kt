package com.gtgt.pokerjacks.ui.webView

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        tv_commonTitle.text = intent.getStringExtra("ACTIVITY_TITLE")
        iv_back.onOneClick {
            finish()
        }
        webView.loadUrl(intent.getStringExtra("ACTIVITY_URL"))
        setupWebView()
        webView?.clearCache(true)
        webView?.clearHistory()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (Build.VERSION.SDK_INT in 21..24) {
            overrideConfiguration!!.uiMode =
                overrideConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {

        val webViewClient: WebViewClient = object : WebViewClient() {


            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)

                return super.shouldOverrideUrlLoading(view, url)
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d("webview", "loading")
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("webview", "loaded")
                super.onPageFinished(view, url)
                view?.clearCache(true)
                view?.clearHistory()
            }
        }
        webView.webViewClient = webViewClient
        webView.settings.javaScriptEnabled = true
        webView.settings.defaultTextEncodingName = "utf-8"
    }
}