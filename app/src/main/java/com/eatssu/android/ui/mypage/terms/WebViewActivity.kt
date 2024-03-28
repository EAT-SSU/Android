package com.eatssu.android.ui.mypage.terms

import android.os.Bundle
import android.webkit.WebViewClient
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityWebviewBinding


class WebViewActivity : BaseActivity<ActivityWebviewBinding>(ActivityWebviewBinding::inflate) {

    companion object {
        const val URL: String = ""
        const val TITLE: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding.webview.loadUrl(URL) // url 주소 가져오기}
//        binding.webview.settings.domStorageEnabled = true
        binding.webview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true

            // localStorage 사용 시
            // webView.settings.domStorageEnabled = true

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
}