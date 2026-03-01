package com.eatssu.android.presentation.mypage.terms

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.View
import android.view.ViewParent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.eatssu.android.BuildConfig
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.component.CloseTopBar
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    title: String,
    url: String,
    screenId: ScreenId,
    backIconResId: Int = -1,
    onBack: () -> Unit,
    onRecreate: () -> Unit,
) {
    val context = LocalContext.current
    val latestOnRecreate = rememberUpdatedState(onRecreate)
    val shouldUseAnyoneButMeSettings = remember(screenId, url) {
        screenId == ScreenId.ANYONE_BUT_ME_MAIN
    }

    val rememberedWebView = remember(context, shouldUseAnyoneButMeSettings) {
        WebView(context).apply {
            configureWebView(
                screenId = screenId,
                initialUrl = url,
                shouldUseAnyoneButMeSettings = shouldUseAnyoneButMeSettings,
                onRenderProcessGone = { latestOnRecreate.value() },
            )
        }
    }

    val rememberedWebViewContainer = remember(context, rememberedWebView) {
        FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            clipToOutline = true
            clipChildren = true
            clipToPadding = true
            setBackgroundColor(Color.WHITE)

            (rememberedWebView.parent as? ViewGroup)?.removeView(rememberedWebView)
            addView(
                rememberedWebView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                ),
            )
        }
    }

    com.eatssu.android.presentation.util.LogScreenView(screenId)

    BackHandler {
        if (rememberedWebView.canGoBack()) {
            rememberedWebView.goBack()
        } else {
            onBack()
        }
    }

    DisposableEffect(rememberedWebView) {
        Timber.i("[WebView] attach screenId=$screenId target=${url.toLogUrl()}")
        rememberedWebView.logViewSnapshot("compose-attached")

        onDispose {
            Timber.i("[WebView] dispose current=${rememberedWebView.url.toLogUrl()}")
            rememberedWebView.logViewSnapshot("compose-dispose")
            runCatching { rememberedWebView.stopLoading() }
            runCatching { rememberedWebView.loadUrl("about:blank") }
            runCatching { (rememberedWebView.parent as? ViewGroup)?.removeView(rememberedWebView) }
            rememberedWebView.webChromeClient = null
            runCatching { rememberedWebView.destroy() }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        WebViewTopBar(
            title = title,
            backIconResId = backIconResId,
            onBack = onBack,
        )

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { rememberedWebViewContainer },
            update = { currentContainer ->
                if (rememberedWebView.parent !== currentContainer) {
                    (rememberedWebView.parent as? ViewGroup)?.removeView(rememberedWebView)
                    currentContainer.removeAllViews()
                    currentContainer.addView(
                        rememberedWebView,
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        ),
                    )

                    Timber.d("[WebView] reattached WebView to FrameLayout container")
                }

                if (rememberedWebView.url != url) {
                    val shouldDeferLoad =
                        !rememberedWebView.isAttachedToWindowSafe() ||
                            rememberedWebView.width == 0 ||
                            rememberedWebView.height == 0

                    if (shouldDeferLoad) {
                        Timber.i(
                            "[WebView] defer loadUrl until attach/layout target=${url.toLogUrl()}",
                        )

                        rememberedWebView.post {
                            if (rememberedWebView.url != url) {
                                Timber.i("[WebView] deferred loadUrl to=${url.toLogUrl()}")
                                rememberedWebView.loadUrl(url)
                            }
                        }
                    } else {
                        Timber.i(
                            "[WebView] update loadUrl from=${rememberedWebView.url.toLogUrl()} to=${url.toLogUrl()}",
                        )
                        rememberedWebView.loadUrl(url)
                    }
                }

                rememberedWebView.logViewSnapshot("androidView-update")
            },
        )
    }
}

@Composable
private fun WebViewTopBar(
    title: String,
    backIconResId: Int,
    onBack: () -> Unit,
) {
    if (backIconResId == -1) {
        EatSsuTopBar(
            title = title,
            onBack = onBack,
        )
    } else {
        CloseTopBar(
            title = title,
            onClose = onBack,
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.configureWebView(
    screenId: ScreenId,
    initialUrl: String,
    shouldUseAnyoneButMeSettings: Boolean,
    onRenderProcessGone: () -> Unit,
) {
    if (BuildConfig.DEBUG) {
        WebView.setWebContentsDebuggingEnabled(true)
    }

    Timber.i(
        "[WebView] init screenId=$screenId initial=${initialUrl.toLogUrl()} anyoneSettings=$shouldUseAnyoneButMeSettings",
    )

    installViewDiagnostics()

    webChromeClient = object : WebChromeClient() {
        private var lastProgressBucket = -1

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            val bucket = when {
                newProgress == 100 -> 100
                newProgress >= 90 -> 90
                newProgress >= 75 -> 75
                newProgress >= 50 -> 50
                newProgress >= 25 -> 25
                else -> 0
            }

            if (bucket != lastProgressBucket) {
                lastProgressBucket = bucket
                Timber.d(
                    "[WebView] progress=$newProgress bucket=$bucket url=${view?.url.toLogUrl()}",
                )
                view?.logViewSnapshot("progress=$newProgress")
            }

            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            Timber.d("[WebView] title=${title.orEmpty()} url=${view?.url.toLogUrl()}")
            super.onReceivedTitle(view, title)
        }

        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?,
        ): Boolean {
            Timber.w(
                "[WebView] onCreateWindow dialog=$isDialog userGesture=$isUserGesture current=${view?.url.toLogUrl()}",
            )
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        }

        override fun onCloseWindow(window: WebView?) {
            Timber.d("[WebView] onCloseWindow url=${window?.url.toLogUrl()}")
            super.onCloseWindow(window)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            Timber.i(
                "[WebView][console][${consoleMessage.messageLevel().toShortName()}] " +
                    "${consoleMessage.message().take(400)} " +
                    "src=${consoleMessage.sourceId().toLogUrl(140)}:${consoleMessage.lineNumber()}",
            )
            return super.onConsoleMessage(consoleMessage)
        }
    }

    webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest,
        ): Boolean {
            Timber.i("[WebView] shouldOverrideUrlLoading ${request.toDebugString()}")
            return false
        }

        override fun onPageStarted(
            view: WebView?,
            url: String?,
            favicon: Bitmap?,
        ) {
            Timber.i(
                "[WebView] onPageStarted url=${url.toLogUrl()} original=${view?.originalUrl.toLogUrl()} main=${view?.url.toLogUrl()}",
            )
            view?.logViewSnapshot("pageStarted")
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageCommitVisible(
            view: WebView?,
            url: String?,
        ) {
            Timber.i(
                "[WebView] onPageCommitVisible url=${url.toLogUrl()} progress=${view?.progress} contentHeight=${view?.contentHeight}",
            )
            view?.logViewSnapshot("commitVisible")
            view?.logDomSnapshot("commitVisible")
            view?.schedulePostRenderSnapshots("commitVisible")

            if (shouldUseAnyoneButMeSettings) {
                view?.nudgeRendering("commitVisible", tryLayerFlip = isProbablyEmulator())
            }

            super.onPageCommitVisible(view, url)
        }

        override fun onPageFinished(
            view: WebView?,
            url: String?,
        ) {
            Timber.i(
                "[WebView] onPageFinished url=${url.toLogUrl()} progress=${view?.progress} contentHeight=${view?.contentHeight} title=${view?.title}",
            )
            view?.logViewSnapshot("pageFinished")
            view?.logDomSnapshot("finished")
            view?.schedulePostRenderSnapshots("finished")

            if (shouldUseAnyoneButMeSettings && view != null && view.contentHeight == 0) {
                view.postDelayed(
                    {
                        if (view.contentHeight == 0) {
                            Timber.w("[WebView] contentHeight still 0 after pageFinished; nudging renderer")
                            view.nudgeRendering("pageFinished-contentHeight0", tryLayerFlip = isProbablyEmulator())
                        }
                    },
                    220L,
                )
            }

            super.onPageFinished(view, url)
        }

        override fun doUpdateVisitedHistory(
            view: WebView?,
            url: String?,
            isReload: Boolean,
        ) {
            Timber.d("[WebView] history url=${url.toLogUrl()} reload=$isReload")
            super.doUpdateVisitedHistory(view, url, isReload)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest,
        ): WebResourceResponse? {
            if (request.isForMainFrame) {
                Timber.d("[WebView] shouldInterceptRequest(mainFrame) ${request.toDebugString()}")
            }
            return super.shouldInterceptRequest(view, request)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {
            val message = "[WebView] onReceivedError ${request.toDebugString()} error=${error.toDebugString()}"
            if (request.isForMainFrame) {
                Timber.e(message)
                view?.logViewSnapshot("mainFrame-error")
            } else {
                Timber.w(message)
            }
            super.onReceivedError(view, request, error)
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse,
        ) {
            val message =
                "[WebView] onReceivedHttpError ${request.toDebugString()} response=${errorResponse.toDebugString()}"
            if (request.isForMainFrame) {
                Timber.e(message)
                view?.logViewSnapshot("mainFrame-httpError")
            } else {
                Timber.w(message)
            }
            super.onReceivedHttpError(view, request, errorResponse)
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?,
        ) {
            Timber.e(
                "[WebView] onReceivedSslError url=${error?.url.toLogUrl()} primary=${error?.primaryError?.toSslErrorName()}",
            )
            view?.logViewSnapshot("sslError")
            super.onReceivedSslError(view, handler, error)
        }

        override fun onRenderProcessGone(
            view: WebView?,
            detail: RenderProcessGoneDetail,
        ): Boolean {
            Timber.e(
                "[WebView] renderProcessGone didCrash=${detail.didCrash()} priority=${detail.rendererPriorityAtExit()}",
            )
            view?.logViewSnapshot("renderProcessGone")

            runCatching { view?.destroy() }
                .onFailure { throwable ->
                    Timber.e(throwable, "[WebView] Error while destroying crashed WebView")
                }

            onRenderProcessGone()
            return true
        }
    }

    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        useWideViewPort = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isAlgorithmicDarkeningAllowed = false
        }

        if (shouldUseAnyoneButMeSettings) {
            loadWithOverviewMode = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            userAgentString = userAgentString
                .replace("; wv", "", ignoreCase = true)
                .replace("Version/4.0 ", "", ignoreCase = true)
        }
    }

    if (shouldUseAnyoneButMeSettings) {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(this@configureWebView, true)
        }
    }

    setDownloadListener { downloadUrl, _, contentDisposition, mimeType, contentLength ->
        Timber.w(
            "[WebView] downloadStart url=${downloadUrl.toLogUrl()} mime=$mimeType disposition=$contentDisposition length=$contentLength",
        )
    }

    setBackgroundColor(Color.WHITE)

    val shouldPreferSoftwareLayer = shouldUseAnyoneButMeSettings && isProbablyEmulator()
    if (shouldPreferSoftwareLayer) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        Timber.w("[WebView] Using SOFTWARE layer fallback on emulator-like device")
    } else {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        Timber.d("[WebView] Using HARDWARE layer")
    }

    Timber.d(
        "[WebView] settings js=${settings.javaScriptEnabled} dom=${settings.domStorageEnabled} " +
            "wide=${settings.useWideViewPort} overview=${settings.loadWithOverviewMode} multi=${settings.supportMultipleWindows()} " +
            "mixed=${settings.mixedContentMode} ua=${settings.userAgentString.toLogUrl(180)}",
    )
}

private fun WebResourceRequest.toDebugString(): String {
    val redirected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) isRedirect else false
    return "url=${url.toString().toLogUrl()} method=$method main=$isForMainFrame redirect=$redirected gesture=${hasGesture()}"
}

private fun WebResourceError.toDebugString(): String {
    return "code=$errorCode description=${description?.toString().orEmpty().take(200)}"
}

private fun WebResourceResponse.toDebugString(): String {
    return "status=$statusCode reason=${reasonPhrase.orEmpty()} mime=${mimeType.orEmpty()} encoding=${encoding.orEmpty()}"
}

private fun ConsoleMessage.MessageLevel.toShortName(): String = when (this) {
    ConsoleMessage.MessageLevel.DEBUG -> "DEBUG"
    ConsoleMessage.MessageLevel.ERROR -> "ERROR"
    ConsoleMessage.MessageLevel.LOG -> "LOG"
    ConsoleMessage.MessageLevel.TIP -> "TIP"
    ConsoleMessage.MessageLevel.WARNING -> "WARN"
}

private fun Int.toSslErrorName(): String = when (this) {
    SslError.SSL_DATE_INVALID -> "SSL_DATE_INVALID"
    SslError.SSL_EXPIRED -> "SSL_EXPIRED"
    SslError.SSL_IDMISMATCH -> "SSL_IDMISMATCH"
    SslError.SSL_INVALID -> "SSL_INVALID"
    SslError.SSL_NOTYETVALID -> "SSL_NOTYETVALID"
    SslError.SSL_UNTRUSTED -> "SSL_UNTRUSTED"
    else -> "UNKNOWN_SSL_ERROR($this)"
}

private fun WebView.logDomSnapshot(stage: String) {
    val script = """
        (function() {
          try {
            var body = document.body;
            var textLen = body && body.innerText ? body.innerText.length : -1;
            var htmlLen = body && body.innerHTML ? body.innerHTML.length : -1;
            var bodyStyle = body ? window.getComputedStyle(body) : null;
            var root = document.documentElement;
            var rootStyle = root ? window.getComputedStyle(root) : null;
            var container = document.getElementById('container');
            var containerStyle = container ? window.getComputedStyle(container) : null;
            var containerFirst = container ? container.firstElementChild : null;
            var containerFirstStyle = containerFirst ? window.getComputedStyle(containerFirst) : null;
            var centerX = Math.floor(window.innerWidth / 2);
            var centerY = Math.floor(window.innerHeight / 2);
            var centerEl = document.elementFromPoint(centerX, centerY);
            var centerStyle = centerEl ? window.getComputedStyle(centerEl) : null;
            var bodyTextSample = body && body.innerText
              ? body.innerText.replace(/\s+/g, ' ').trim().slice(0, 160)
              : "";
            var centerTextSample = centerEl && centerEl.innerText
              ? centerEl.innerText.replace(/\s+/g, ' ').trim().slice(0, 120)
              : "";
            var fontsStatus = document.fonts && document.fonts.status ? document.fonts.status : "";
            return JSON.stringify({
              readyState: document.readyState,
              title: document.title || "",
              textLen: textLen,
              htmlLen: htmlLen,
              visibility: document.visibilityState || "",
              hasBody: !!body,
              href: window.location.href,
              innerWidth: window.innerWidth,
              innerHeight: window.innerHeight,
              bodyBg: bodyStyle ? bodyStyle.backgroundColor : "",
              bodyColor: bodyStyle ? bodyStyle.color : "",
              bodyDisplay: bodyStyle ? bodyStyle.display : "",
              bodyOpacity: bodyStyle ? bodyStyle.opacity : "",
              rootBg: rootStyle ? rootStyle.backgroundColor : "",
              rootColor: rootStyle ? rootStyle.color : "",
              rootDisplay: rootStyle ? rootStyle.display : "",
              rootOpacity: rootStyle ? rootStyle.opacity : "",
              bodyTextSample: bodyTextSample,
              fontsStatus: fontsStatus,
              containerChildren: container ? container.childElementCount : -1,
              containerTextLen: container && container.innerText ? container.innerText.replace(/\s+/g, ' ').trim().length : -1,
              containerHtmlLen: container && container.innerHTML ? container.innerHTML.length : -1,
              containerDisplay: containerStyle ? containerStyle.display : "",
              containerVisibility: containerStyle ? containerStyle.visibility : "",
              containerOpacity: containerStyle ? containerStyle.opacity : "",
              containerBg: containerStyle ? containerStyle.backgroundColor : "",
              containerFirstTag: containerFirst ? containerFirst.tagName : "",
              containerFirstClass: containerFirst ? (containerFirst.className || "") : "",
              containerFirstDisplay: containerFirstStyle ? containerFirstStyle.display : "",
              containerFirstVisibility: containerFirstStyle ? containerFirstStyle.visibility : "",
              containerFirstOpacity: containerFirstStyle ? containerFirstStyle.opacity : "",
              containerFirstBg: containerFirstStyle ? containerFirstStyle.backgroundColor : "",
              canvasCount: document.getElementsByTagName('canvas').length,
              svgCount: document.getElementsByTagName('svg').length,
              centerTag: centerEl ? centerEl.tagName : "",
              centerId: centerEl ? (centerEl.id || "") : "",
              centerClass: centerEl ? (centerEl.className || "") : "",
              centerTextSample: centerTextSample,
              centerBg: centerStyle ? centerStyle.backgroundColor : "",
              centerColor: centerStyle ? centerStyle.color : "",
              centerDisplay: centerStyle ? centerStyle.display : "",
              centerVisibility: centerStyle ? centerStyle.visibility : "",
              centerOpacity: centerStyle ? centerStyle.opacity : ""
            });
          } catch (e) {
            return "JS_ERROR:" + (e && e.message ? e.message : e);
          }
        })();
    """.trimIndent()

    evaluateJavascript(script) { result ->
        Timber.d("[WebView][dom][$stage] ${result.toLogUrl(1800)}")
    }
}

private fun String?.toLogUrl(maxLen: Int = 260): String {
    val value = this.orEmpty().replace('\n', ' ')
    return if (value.length <= maxLen) value else value.take(maxLen) + "..."
}

private fun WebView.installViewDiagnostics() {
    addOnAttachStateChangeListener(
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                Timber.i("[WebView][view] attachedToWindow")
                logViewSnapshot("onAttachStateChange(attached)")
            }

            override fun onViewDetachedFromWindow(v: View) {
                Timber.i("[WebView][view] detachedFromWindow")
                logViewSnapshot("onAttachStateChange(detached)")
            }
        },
    )

    addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        val changed = left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom
        if (changed) {
            Timber.d(
                "[WebView][view] layout new=($left,$top,$right,$bottom) old=($oldLeft,$oldTop,$oldRight,$oldBottom)",
            )
            logViewSnapshot("onLayoutChange")
        }
    }

    setOnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->
        Timber.d(
            "[WebView][view] scroll new=($scrollX,$scrollY) old=($oldScrollX,$oldScrollY) contentHeight=$contentHeight",
        )
    }
}

private fun WebView.logViewSnapshot(stage: String) {
    val visibleRect = Rect()
    val hasVisibleRect = getGlobalVisibleRect(visibleRect)
    val location = IntArray(2)
    getLocationOnScreen(location)

    val visibilityName = visibility.toVisibilityName()
    val windowVisibilityName = windowVisibility.toVisibilityName()
    val parentPath = parent.toParentPath()

    Timber.d(
        "[WebView][view][$stage] " +
            "attached=${isAttachedToWindowSafe()} shown=$isShown vis=$visibilityName windowVis=$windowVisibilityName " +
            "alpha=$alpha layer=${layerType.toLayerTypeName()} hw=$isHardwareAccelerated " +
            "size=${width}x$height measured=${measuredWidth}x$measuredHeight " +
            "loc=${location[0]},${location[1]} globalVisible=$hasVisibleRect rect=${visibleRect.toShortString()} " +
            "progress=$progress contentHeight=$contentHeight scroll=${scrollX},${scrollY} " +
            "canScrollUp=${canScrollVertically(-1)} canScrollDown=${canScrollVertically(1)} parent=$parentPath",
    )
}

private fun WebView.schedulePostRenderSnapshots(stage: String) {
    val delays = longArrayOf(250L, 750L, 1500L)

    delays.forEach { delayMs ->
        postDelayed(
            {
                if (!isAttachedToWindowSafe()) {
                    Timber.d("[WebView][view][$stage+$delayMs] skipped (not attached)")
                    return@postDelayed
                }

                logViewSnapshot("$stage+$delayMs")
                logDomSnapshot("$stage+$delayMs")
            },
            delayMs,
        )
    }
}

private fun WebView.nudgeRendering(stage: String, tryLayerFlip: Boolean) {
    Timber.w(
        "[WebView] nudgeRendering stage=$stage layer=${layerType.toLayerTypeName()} tryLayerFlip=$tryLayerFlip",
    )

    post {
        requestLayout()
        invalidate()

        if (tryLayerFlip) {
            val originalLayer = layerType
            val temporaryLayer = if (originalLayer == View.LAYER_TYPE_HARDWARE) {
                View.LAYER_TYPE_SOFTWARE
            } else {
                View.LAYER_TYPE_HARDWARE
            }

            setLayerType(temporaryLayer, null)
            Timber.w(
                "[WebView] nudgeRendering temporary layer=${temporaryLayer.toLayerTypeName()} original=${originalLayer.toLayerTypeName()}",
            )

            postDelayed(
                {
                    setLayerType(originalLayer, null)
                    requestLayout()
                    invalidate()
                    Timber.w("[WebView] nudgeRendering restored layer=${originalLayer.toLayerTypeName()}")
                },
                90L,
            )
        }
    }
}

private fun isProbablyEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT.lowercase()
    val model = Build.MODEL.lowercase()
    val product = Build.PRODUCT.lowercase()
    val hardware = Build.HARDWARE.lowercase()
    val manufacturer = Build.MANUFACTURER.lowercase()

    return fingerprint.contains("generic") ||
        fingerprint.contains("emulator") ||
        model.contains("emulator") ||
        model.contains("sdk_gphone") ||
        product.contains("sdk") ||
        product.contains("emulator") ||
        hardware.contains("ranchu") ||
        manufacturer.contains("genymotion")
}

private fun Int.toVisibilityName(): String = when (this) {
    View.VISIBLE -> "VISIBLE"
    View.INVISIBLE -> "INVISIBLE"
    View.GONE -> "GONE"
    else -> "UNKNOWN($this)"
}

private fun Int.toLayerTypeName(): String = when (this) {
    View.LAYER_TYPE_NONE -> "NONE"
    View.LAYER_TYPE_SOFTWARE -> "SOFTWARE"
    View.LAYER_TYPE_HARDWARE -> "HARDWARE"
    else -> "UNKNOWN($this)"
}

private fun ViewParent?.toParentPath(): String {
    val names = mutableListOf<String>()
    var current = this
    var depth = 0

    while (current != null && depth < 8) {
        names += current.javaClass.simpleName
        current = current.parent
        depth += 1
    }

    return if (names.isEmpty()) "none" else names.joinToString(" > ")
}

private fun View.isAttachedToWindowSafe(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        isAttachedToWindow
    } else {
        windowToken != null
    }
}

@Preview
@Composable
private fun WebViewScreenPreview() {
    EatssuTheme {
        WebViewScreen(
            title = "이용약관",
            url = "https://example.com",
            screenId = ScreenId.EXTERNAL_TERMS,
            onBack = {},
            onRecreate = {},
        )
    }
}
