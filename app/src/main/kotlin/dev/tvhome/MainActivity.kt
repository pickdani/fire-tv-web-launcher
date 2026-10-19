package dev.tvhome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * The home screen: a full-screen WebView that shows the home page.
 * [HomeFiles] serves the page from https://tvhome.local/, and the page calls Android through [TvBridge].
 */
class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var homeFiles: HomeFiles

    private val packagesChanged = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            homeFiles.clearIconCache()
            callPage("onAppsChanged")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFiles = HomeFiles(this)

        webView = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            // Honor <meta name="viewport" content="width=1920">, so 1 CSS px = 1 screen px at 1080p.
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            addJavascriptInterface(TvBridge(this@MainActivity), "TV")
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? =
                    homeFiles.serve(request.url)

                // Links to other sites do nothing. The home page stays in place.
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                    request.url.host != HomeFiles.HOST
            }
        }
        setContentView(webView)
        webView.loadUrl(homeFiles.startUrl())

        registerReceiver(packagesChanged, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        })
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        callPage("onShow")
    }

    override fun onPause() {
        callPage("onHide")
        webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        unregisterReceiver(packagesChanged)
        webView.destroy()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // The tools/ scripts ask for a reload this way. They must not force-stop the app:
        // Android turns off an app's accessibility service when the app is force-stopped.
        if (intent.getBooleanExtra(EXTRA_RELOAD, false)) {
            reloadPage()
        } else {
            callPage("onHome") // Home pushed while the home screen is already in front.
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // A home screen must not close on Back. The page gets onBack() instead.
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_UP) callPage("onBack")
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    fun reloadPage() {
        runOnUiThread { webView.loadUrl(homeFiles.startUrl()) }
    }

    /** Calls window.<name>() on the page, if the page defines it. */
    private fun callPage(name: String) {
        if (!::webView.isInitialized) return
        webView.post { webView.evaluateJavascript("window.$name && window.$name()", null) }
    }

    companion object {
        const val EXTRA_RELOAD = "reload"
    }
}
