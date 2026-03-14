package com.eatssu.android.presentation.mypage.terms

import android.os.Bundle
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityWebviewBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.common.analytics.ScreenViewEvent
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WebViewActivity :
    BaseActivity<ActivityWebviewBinding>(
        ActivityWebviewBinding::inflate,
        ScreenId.EXTERNAL_INQUIRE // shouldLogScreenId가 false라 미사용
    ) {

    private var URL = ""
    private var TITLE = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyBackIconFromIntent()

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

            // 웹 페이지에서 새 창을 열 수 있도록 설정
            // Notion 페이지 = DOM Storage(domStorageEnabled) 없으면 동작 불가
            settings.apply {
                javaScriptEnabled = true // WebView에서 JavaScript 실행을 허용
                domStorageEnabled = true // localStorage, sessionStorage 활성화
                useWideViewPort = true // 화면 크기에 맞게 웹 페이지를 조정
            }

            URL = intent.getStringExtra(EXTRA_URL) ?: "" //Todo 뷰모델 사용하도록 수정?
            TITLE = intent.getStringExtra(EXTRA_TITLE) ?: ""

            toolbarTitle.text = TITLE
            Timber.d(URL + TITLE)

            if (savedInstanceState != null) restoreState(savedInstanceState)
            else loadUrl(URL)
        }
    }

    private fun applyBackIconFromIntent() {
        if (intent.hasExtra(EXTRA_BACK_ICON_RES_ID)) {
            val backIconResId = intent.getIntExtra(EXTRA_BACK_ICON_RES_ID, 0)
            if (backIconResId != 0) {
                findViewById<ImageView>(R.id.btn_back).setImageResource(backIconResId)
            }
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

        analyticsTracker.track(ScreenViewEvent(screenId))
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

    companion object {
        const val EXTRA_URL = "URL"
        const val EXTRA_TITLE = "TITLE"
        const val EXTRA_BACK_ICON_RES_ID = "BACK_ICON_RES_ID"
    }
}
