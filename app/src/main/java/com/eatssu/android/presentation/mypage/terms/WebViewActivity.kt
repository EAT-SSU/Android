package com.eatssu.android.presentation.mypage.terms

import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityWebviewBinding


class WebViewActivity : BaseActivity<ActivityWebviewBinding>(ActivityWebviewBinding::inflate) {

    companion object {
        val TAG = "WebViewActivity"
    }

    private var URL = ""
    private var TITLE = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding.webview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true

            // localStorage 사용 시
            // webView.settings.domStorageEnabled = true

            URL = intent.getStringExtra("URL") ?: "" //Todo 뷰모델 사용하도록 수정?
            TITLE = intent.getStringExtra("TITLE") ?: ""

            toolbarTitle.text = TITLE
            Log.d(TAG, URL + TITLE)

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