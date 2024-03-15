package com.eatssu.android.ui.mypage.terms

import android.net.http.SslError
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityWebviewBinding


class WebViewActivity() : BaseActivity<ActivityWebviewBinding>(ActivityWebviewBinding::inflate) {

    companion object {
        const val URL: String = ""
        const val TITLE: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflate : xml의 뷰를 객체화 해준다.
//        val myWebView= binding.webview
//        myWebView.loadUrl("www.naver.com")
//
//        binding.webview.settings.apply {
//            this.setSupportMultipleWindows(false) // 새창 띄우기 허용
//            this.setSupportZoom(false) // 화면 확대 허용.setDomStorageEnabled(true);
//            this.javaScriptEnabled = true // 자바스크립트 허용
//            this.javaScriptCanOpenWindowsAutomatically = false // 자바스크립트 새창 띄우기 허용
//            this.loadWithOverviewMode = true // html의 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
//            this.useWideViewPort = true // html의 viewport 메타 태그 지원
//            this.builtInZoomControls = false // 화면 확대/축소 허용
//            this.displayZoomControls = false
//            this.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN // 컨텐츠 사이즈 맞추기
//            this.cacheMode = WebSettings.LOAD_NO_CACHE // 브라우저 캐쉬 허용
//            this.domStorageEnabled = true // 로컬 저장 허용
//            this.databaseEnabled = true
//
//            /**
//             * This request has been blocked; the content must be served over HTTPS
//             * https 에서 이미지가 표시 안되는 오류를 해결하기 위한 처리
//             */
//            this.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//        }
//
//        webView.webViewClient = WebViewClient()
//
//        binding.webview.apply {
//            webViewClient = WebViewClient()
//            // 페이지 컨트롤을 위한 기본적인 함수, 다양한 요청, 알림을 수신하는 기능
//            settings.javaScriptEnabled = true // 자바스크립트 허용
//            webview.setDomStorageEnabled(true);
////        webView.
//        }
        binding.webview.loadUrl(URL) // url 주소 가져오기}

        binding.webview.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler, error: SslError,
            ) {
                handler.proceed()
            }
        }
    }

    fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
        handler.proceed() // SSL 에러가 발생해도 계속 진행
    }

}