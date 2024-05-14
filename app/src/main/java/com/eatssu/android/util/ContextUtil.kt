package com.eatssu.android.util

import android.content.Context
import android.widget.Toast

fun Context.showToast(msg: String) {
    //Todo 앱 진입시 빈 토스트 왜 뜨는지 알아야함
    if (msg.isNotEmpty()) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}