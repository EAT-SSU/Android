package com.eatssu.android.ui.common


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class ForceUpdateDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showForceUpdateDialog()
    }

    private fun showForceUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("강제 업데이트")
        builder.setMessage("새 버전의 앱을 설치해야 합니다.")

        builder.setPositiveButton("업데이트") { dialog, which ->
            // Google Play Store의 앱 페이지로 이동하여 업데이트를 다운로드합니다.
            val appPackageName = packageName
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }

            // 다이얼로그를 종료합니다.
            finish()
        }

        builder.setCancelable(false) // 사용자가 다이얼로그를 취소할 수 없도록 설정

        val dialog = builder.create()
        dialog.show()
    }
}