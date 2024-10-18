package com.eatssu.android.ui.common


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class AndroidMessageDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDialog()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("공지")
        val message = intent.getStringExtra("message")
        Log.d("message",message.toString())
        builder.setMessage(intent.getStringExtra("message"))

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