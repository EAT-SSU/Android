package com.eatssu.android.ui.mypage.terms

import android.os.Bundle
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivityWebviewBinding


class WebViewActivity : BaseActivity<ActivityWebviewBinding>(ActivityWebviewBinding::inflate) {

    companion object {
        const val URL: String = ""
        const val TITLE: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.webview.loadUrl(URL) // url 주소 가져오기}
        binding.webview.settings.domStorageEnabled = true

    }
}