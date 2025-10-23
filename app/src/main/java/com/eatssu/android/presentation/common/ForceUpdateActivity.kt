package com.eatssu.android.presentation.common

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

/**
 * 강제 업데이트 다이얼로그를 표시하는 액티비티
 *
 * Firebase Remote Config에서 설정한 최소 버전보다 현재 앱 버전이 낮을 경우
 * 이 액티비티가 표시되며, 사용자는 반드시 업데이트를 해야 앱을 사용할 수 있습니다.
 */
class ForceUpdateDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showForceUpdateDialog()
    }

    private fun showForceUpdateDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("업데이트가 필요합니다")
            setMessage("원활한 서비스 이용을 위해\n최신 버전으로 업데이트해 주세요.")
            setPositiveButton("업데이트") { _, _ ->
                openPlayStore()
                finish()
            }
            setCancelable(false)
            create()
        }.show()
    }

    private fun openPlayStore() {
        val appPackageName = packageName
        try {
            // Google Play 앱으로 직접 이동
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=$appPackageName".toUri()
                )
            )
        } catch (_: ActivityNotFoundException) {
            // Play 앱이 없는 경우 웹 브라우저로 이동
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                )
            )
        }
    }
}