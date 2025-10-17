package com.eatssu.android.presentation.common


import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.presentation.base.ActivityCompanionWithArgs
import kotlinx.parcelize.Parcelize
import timber.log.Timber


class AndroidMessageDialogActivity : AppCompatActivity() {

    @Parcelize
    data class Args(val message: String) : Parcelable

    companion object :
        ActivityCompanionWithArgs<Args>(AndroidMessageDialogActivity::class, Args::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDialog()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("공지")
        val message = intentOptions?.message
        Timber.tag("message").d(message.toString())
        builder.setMessage(message)

        builder.setPositiveButton("확인") { dialog, which ->
            // Google Play Store의 앱 페이지로 이동하여 업데이트를 다운로드합니다.

            // 다이얼로그를 종료합니다.
            finish()
        }

        builder.setCancelable(false) // 사용자가 다이얼로그를 취소할 수 없도록 설정

        val dialog = builder.create()
        dialog.show()
    }
}