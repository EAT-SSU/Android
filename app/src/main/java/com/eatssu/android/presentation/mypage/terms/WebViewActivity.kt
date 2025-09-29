package com.eatssu.android.presentation.mypage.terms

import android.os.Bundle
import android.webkit.WebViewClient
import com.eatssu.android.databinding.ActivityWebviewBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.ScreenId
import timber.log.Timber


class WebViewActivity :
    BaseActivity<ActivityWebviewBinding>(
        ActivityWebviewBinding::inflate,
        ScreenId.EXTERNAL_INQUIRE // shouldLogScreenId가 false라 미사용
    ) {


    private var URL = ""
    private var TITLE = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding.webview.apply {
            webViewClient = WebViewClient()

            // localStorage 사용 시
            // webView.settings.domStorageEnabled = true

            // 웹 페이지에서 새 창을 열 수 있도록 설정
            // Notion 페이지 = DOM Storage(domStorageEnabled) 없으면 동작 불가
            settings.apply {
                javaScriptEnabled = true // WebView에서 JavaScript 실행을 허용
                domStorageEnabled = true // localStorage, sessionStorage 활성화
                useWideViewPort = true // 화면 크기에 맞게 웹 페이지를 조정
            }

            URL = intent.getStringExtra("URL") ?: "" //Todo 뷰모델 사용하도록 수정?
            TITLE = intent.getStringExtra("TITLE") ?: ""

            toolbarTitle.text = TITLE
            Timber.d(URL + TITLE)

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

    override fun shouldLogScreenId() = false
}