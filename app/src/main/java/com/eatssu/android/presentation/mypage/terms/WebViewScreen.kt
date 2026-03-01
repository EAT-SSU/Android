package com.eatssu.android.presentation.mypage.terms

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.eatssu.android.presentation.util.LogScreenView
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.component.CloseTopBar
import com.eatssu.design_system.component.EatSsuTopBar
import com.eatssu.design_system.theme.EatssuTheme

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

    val webView = remember(context) {
        WebView(context).apply {
            configureWebView(
                onRenderProcessGone = { latestOnRecreate.value() },
            )
        }
    }

    val webViewContainer = remember(context, webView) {
        FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            clipToOutline = true
            clipChildren = true
            clipToPadding = true
            setBackgroundColor(Color.WHITE)

            (webView.parent as? ViewGroup)?.removeView(webView)
            addView(
                webView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                ),
            )
        }
    }

    LogScreenView(screenId)

    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            onBack()
        }
    }

    DisposableEffect(webView) {
        onDispose {
            runCatching { webView.stopLoading() }
            runCatching { (webView.parent as? ViewGroup)?.removeView(webView) }
            webView.webChromeClient = null
            runCatching { webView.destroy() }
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
            factory = { webViewContainer },
            update = { currentContainer ->
                if (webView.parent !== currentContainer) {
                    (webView.parent as? ViewGroup)?.removeView(webView)
                    currentContainer.removeAllViews()
                    currentContainer.addView(
                        webView,
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        ),
                    )
                }
                webView.loadUrl(url)
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
    onRenderProcessGone: () -> Unit,
) {
    webViewClient = object : WebViewClient() {
        override fun onRenderProcessGone(
            view: WebView?,
            detail: RenderProcessGoneDetail,
        ): Boolean {
            runCatching { view?.destroy() }
            onRenderProcessGone()
            return true
        }
    }

    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        useWideViewPort = true
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
