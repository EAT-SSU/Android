package com.eatssu.android.presentation.mypage.terms

import android.annotation.SuppressLint
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.eatssu.design_system.preview.ThemePreviews
import androidx.compose.ui.viewinterop.AndroidView
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
    var webView by remember { mutableStateOf<WebView?>(null) }

    com.eatssu.android.presentation.util.LogScreenView(screenId)

    BackHandler {
        val wv = webView
        if (wv != null && wv.canGoBack()) {
            wv.goBack()
        } else {
            onBack()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.apply {
                stopLoading()
                webChromeClient = null
                destroy()
            }
        }
    }

    Scaffold(
        topBar = {
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
        },
    ) { innerPadding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onRenderProcessGone(
                            view: WebView?,
                            detail: RenderProcessGoneDetail,
                        ): Boolean {
                            Timber.e("⚠WebView renderer crashed! Did renderer die: ${detail.didCrash()}")
                            try {
                                view?.destroy()
                            } catch (e: Exception) {
                                Timber.e(e, "Error while destroying crashed WebView")
                            }
                            onRecreate()
                            return true
                        }
                    }
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        useWideViewPort = true
                    }
                    loadUrl(url)
                    webView = this
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@ThemePreviews
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
