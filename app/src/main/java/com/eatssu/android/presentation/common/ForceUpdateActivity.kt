package com.eatssu.android.presentation.common


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.eatssu.android.presentation.util.showDialog


class ForceUpdateDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showForceUpdateDialog()
    }

    private fun showForceUpdateDialog() {
        showDialog("강제 업데이트", "새 버전의 앱을 설치해야 합니다.") {
            confirmText = "업데이트"
            cancellable = false
            showCancelButton = false

            onConfirm {
                // Google Play Store의 앱 페이지로 이동하여 업데이트를 다운로드합니다.
                val appPackageName = packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "market://details?id=$appPackageName".toUri()
                        )
                    )
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                        )
                    )
                }

                // 다이얼로그를 종료합니다.
                finish()
            }
        }
    }
}