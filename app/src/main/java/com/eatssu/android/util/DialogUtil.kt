package com.eatssu.android.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object DialogUtil {

//    fun createDialog(positiveMessage: String,
//                     message: String,
//                     context: Context,
//                     listener: DialogInterface.OnClickListener? = null
//    ) {
//        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
//        builder.setMessage(message)
//        builder.setPositiveButton(positiveMessage, listener) { dialogInterface, i ->
//            dialogInterface.dismiss()
//        }
//        builder.create().show()
//    }


    fun createDialogWithTitleAndMessage(
        title: String,
        message: String,
        context: Context,
        listener: DialogInterface.OnClickListener? = null,
    ) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("확인", listener)
        builder.create().show()
    }


    fun createDialogWithMessage(message: String, context: Context) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton("확인") { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        builder.create().show()
    }

//    fun createDialogWithTitleAndMessage(
//        title: String,
//        message: String,
//        context: Context,
//        listener: DialogInterface.OnClickListener? = null,
//    ) {
//        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
//        builder.setTitle(title)
//        builder.setMessage(message)
//        builder.setPositiveButton("확인", listener)
//        builder.create().show()
//    }

    fun createDialogWithCancelButton(
        title: String,
        context: Context,
        message: String? = null,
        cancelText: String,
        confirmText: String,
        positiveButtonListener: DialogInterface.OnClickListener? = null,
        negativeButtonListener: DialogInterface.OnClickListener? = null,
    ) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setCancelable(true)
        if (message != null) builder.setMessage(message)
        builder.setNegativeButton(cancelText, negativeButtonListener)
        builder.setPositiveButton(confirmText, positiveButtonListener)
        builder.create().show()
    }

    fun createDialogWithCancelButtonAndMessage(
        title: String,
        message: String,
        context: Context,
        cancelText: String,
        confirmText: String,
        listener: DialogInterface.OnClickListener? = null,
    ) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(true)
        builder.setNegativeButton(
            cancelText,
            DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            },
        )
        builder.setPositiveButton(confirmText, listener)
        builder.create().show()
    }

//    fun createDialogWithEditText(
//        context: Context,
//        title: String,
//        callback: (String) -> Unit,
//        editText: EditText? = null,
//    ) {
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle(title)
//        editText?.let {
//            val container = FrameLayout(context)
//            val params = FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//            )
////            params.leftMargin = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
////            params.rightMargin = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
//            it.layoutParams = params
//            container.addView(it)
//            builder.setView(container)
//        }
//        builder.setPositiveButton(
//            "확인",
//        ) { dialog, which ->
//            dialog.dismiss()
//            editText?.let {
//                val strText = editText.text.toString()
//                callback.invoke(strText)
//            }
//        }
//        builder.setNegativeButton(
//            "취소",
//        ) { dialog, which -> dialog.cancel() }
//        builder.show()
//    }
}
