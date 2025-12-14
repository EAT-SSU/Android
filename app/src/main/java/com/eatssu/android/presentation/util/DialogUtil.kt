package com.eatssu.android.presentation.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.toDrawable
import com.eatssu.android.R

class DialogBuilder(
    private val context: Context,

    // Dialog에 표시할 제목
    private val title: String,

    // Dialog에 표시할 설명
    private val description: String
) {
    // 확인 버튼을 눌렀을 때 동작
    private var onConfirm: (dialog: Dialog) -> Unit = { it.dismiss() }

    // 취소 버튼을 눌렀을 때 동작
    private var onCancel: (dialog: Dialog) -> Unit = { it.dismiss() }

    // Dialog가 닫힐 때 동작
    private var onDismiss: () -> Unit = {}

    // Dialog 바깥을 누르면 닫히는지 여부
    var cancellable: Boolean = true

    // 확인 버튼 텍스트
    var confirmText: String = context.getString(R.string.confirm)

    // 취소 버튼 텍스트
    var cancelText: String = context.getString(R.string.cancel)

    // 취소 버튼 표시 여부
    var showCancelButton: Boolean = true

    // Destructive한 동작인지 여부 - 확인/취소 버튼 위치 변경, 확인 색 Error 색 변경
    var isDestructive: Boolean = false

    // Dialog를 생성 후 바로 열 것인지 여부
    var showWhenStart: Boolean = true

    fun onConfirm(action: (dialog: Dialog) -> Unit) = apply {
        this.onConfirm = action
    }

    fun onCancel(action: (dialog: Dialog) -> Unit) = apply {
        this.onCancel = action
    }

    fun onDismiss(action: () -> Unit) = apply {
        this.onDismiss = action
    }

    fun show(): Dialog {
        val dialog = Dialog(context)

        // Dialog Radius 적용
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(if (isDestructive) R.layout.dialog_destructive else R.layout.dialog_default)

        // UI 설정
        dialog.findViewById<TextView>(R.id.dialog_title).text = title
        dialog.findViewById<TextView>(R.id.dialog_description).text = description

        val confirmButton = dialog.findViewById<Button>(R.id.dialog_confirm_btn)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancel_btn)

        confirmButton.text = confirmText
        confirmButton.setOnClickListener {
            onConfirm(dialog)
        }

        if (showCancelButton) {
            cancelButton.text = cancelText
            cancelButton.setOnClickListener {
                onCancel(dialog)
            }
        } else {
            cancelButton.visibility = android.view.View.GONE
        }

        dialog.setCancelable(cancellable)
        dialog.setOnDismissListener {
            onDismiss()
        }

        if (showWhenStart)
            dialog.show()

        return dialog
    }
}

fun Context.showDialog(
    title: String,
    description: String,
    builder: DialogBuilder.() -> Unit = {}
): Dialog {
    return DialogBuilder(this, title, description).apply(builder).show()
}


fun Context.showDialog(
    @StringRes titleId: Int,
    @StringRes descriptionId: Int,
    builder: DialogBuilder.() -> Unit = {}
): Dialog {
    return DialogBuilder(
        this,
        getString(titleId),
        getString(descriptionId)
    ).apply(builder).show()
}