package com.eatssu.android.presentation.util

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.R
import com.eatssu.android.presentation.base.NetworkErrorEventBus
import com.eatssu.android.presentation.error.ServerErrorActivity
import kotlinx.coroutines.launch

inline fun <reified T : Activity> AppCompatActivity.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}

/**
 * NetworkErrorEventBus를 구독하여 네트워크 에러 발생 시 ServerErrorActivity로 이동합니다.
 */
fun AppCompatActivity.observeNetworkError(
    errorTitle: String? = null,
    errorMessage: String? = null
) {
    lifecycleScope.launch {
        NetworkErrorEventBus.networkError.collect {
            val intent = Intent(this@observeNetworkError, ServerErrorActivity::class.java).apply {
                putExtra(
                    ServerErrorActivity.EXTRA_TITLE,
                    errorTitle ?: getString(R.string.server_error_title)
                )
                putExtra(
                    ServerErrorActivity.EXTRA_MESSAGE,
                    errorMessage ?: getString(R.string.server_error_message)
                )
            }
            startActivity(intent)
            finish()
        }
    }
}