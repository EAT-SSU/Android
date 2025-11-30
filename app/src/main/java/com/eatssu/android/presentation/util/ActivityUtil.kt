package com.eatssu.android.presentation.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.R
import com.eatssu.android.presentation.base.NetworkErrorEventBus
import kotlinx.coroutines.launch

inline fun <reified T : Activity> AppCompatActivity.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}

/**
 * NetworkErrorEventBus를 구독하여 네트워크 에러 발생 시 다이얼로그를 표시합니다.
 * 액티비티를 종료하지 않아 진행 중인 요청이 취소되지 않습니다.
 */
fun AppCompatActivity.observeNetworkError(
    errorTitle: String? = null,
    errorMessage: String? = null
) {
    var networkErrorDialog: AlertDialog? = null

    lifecycleScope.launch {
        NetworkErrorEventBus.networkError.collect {
            if (networkErrorDialog?.isShowing == true) {
                return@collect
            }

            val title = errorTitle ?: getString(R.string.server_error_title)
            val message = errorMessage ?: getString(R.string.server_error_message)

            networkErrorDialog = AlertDialog.Builder(this@observeNetworkError)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .create()
                .also { dialog ->
                    dialog.setOnDismissListener {
                        networkErrorDialog = null
                    }
                    dialog.show()
                }
        }
    }
}