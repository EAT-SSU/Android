package com.eatssu.android.presentation.common

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.presentation.util.showDialog

class AndroidMessageDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDialog()
    }

    private fun showDialog() {
        val message = intent.getStringExtra("message")
        Log.d("message", message.toString())

        showDialog(
            title = "공지",
            description = message ?: ""
        ) {
            confirmText = "확인"
            showCancelButton = false
            cancellable = false
            onConfirm { dialog ->
                dialog.dismiss()
                finish()
            }
        }
    }
}