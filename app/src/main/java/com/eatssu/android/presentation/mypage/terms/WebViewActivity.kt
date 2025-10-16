package com.eatssu.android.presentation.mypage.terms

import android.os.Bundle
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.webkit.WebViewClient
import com.eatssu.android.databinding.ActivityWebviewBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.ScreenId
import timber.log.Timber

class WebViewActivity :
    BaseActivity<ActivityWebviewBinding>(
        ActivityWebviewBinding::inflate,
        ScreenId.EXTERNAL_INQUIRE
    ) {

    private var URL = ""
    private var TITLE = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.webview.apply {
            webViewClient = object : WebViewClient() {

                // 렌더러 충돌 시 호출되는 콜백 (Android 8.0 이상)
                override fun onRenderProcessGone(
                    view: WebView?,
                    detail: RenderProcessGoneDetail
                ): Boolean {
                    Timber.e("⚠WebView renderer crashed! Did renderer die: ${detail.didCrash()}")

                    // view가 null이거나 이미 죽은 상태이므로 안전하게 정리
                    try {
                        view?.destroy()
                    } catch (e: Exception) {
                        Timber.e(e, "Error while destroying crashed WebView")
                    }

                    // WebView를 재생성하거나 오류 안내 UI를 표시
                    recreate() // Activity 재시작으로 복구
                    return true // 앱 강제 종료 방지
                }
            }

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                useWideViewPort = true
            }

            URL = intent.getStringExtra("URL") ?: "" //Todo 뷰모델 사용하도록 수정?
            TITLE = intent.getStringExtra("TITLE") ?: ""

            toolbarTitle.text = TITLE
            Timber.d("Loading WebView: $URL ($TITLE)")

            if (savedInstanceState != null) restoreState(savedInstanceState)
            else loadUrl(URL)
        }
    }

    override fun onBackPressed() {
        if (binding.webview.canGoBack()) binding.webview.goBack()
        else super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webview.saveState(outState)
    }

    override fun onResume() {
        super.onResume()

        val screenIdString = intent.getStringExtra("SCREEN_ID") ?: return
        val screenId = ScreenId.entries.find { it.name == screenIdString } ?: return

        EventLogger.screenView(screenId)
        Timber.d("WebViewActivity screen view logging: $screenId")
    }

    override fun onDestroy() {
        binding.webview.apply {
            stopLoading()
            webChromeClient = null
            destroy()
        }
        super.onDestroy()
    }

    override fun shouldLogScreenId() = false
}
