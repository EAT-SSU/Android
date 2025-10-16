package com.eatssu.android.presentation.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Window
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.eatssu.android.R

fun Context.openOkCancelDialog(
    title: String,
    description: String,
    onConfirm: (dialog: Dialog) -> Unit = {},
    onCancel: (dialog: Dialog) -> Unit = {}
) {
    val dialog = Dialog(this)

    // Dialog Radius 적용
    dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_ok_cancel)

    // UI 설정
    dialog.findViewById<TextView>(R.id.dialog_title).text = title
    dialog.findViewById<TextView>(R.id.dialog_description).text = description

    dialog.findViewById<TextView>(R.id.dialog_confirm_btn).setOnClickListener {
        // 확인 버튼 클릭 시 동작
        onConfirm(dialog)
    }

    dialog.findViewById<TextView>(R.id.dialog_cancel_btn).setOnClickListener {
        // 취소 버튼 클릭 시 동작
        onCancel(dialog)
    }

    dialog.show()
}

fun Context.openOkDialog(
    title: String,
    description: String,
    onConfirm: (dialog: Dialog) -> Unit = {}
) {
    val dialog = Dialog(this)

    // Dialog Radius 적용
    dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_ok)

    // UI 설정
    dialog.findViewById<TextView>(R.id.dialog_title).text = title
    dialog.findViewById<TextView>(R.id.dialog_description).text = description

    dialog.findViewById<TextView>(R.id.dialog_confirm_btn).setOnClickListener {
        // 확인 버튼 클릭 시 동작
        onConfirm(dialog)
    }

    dialog.show()
}