package com.eatssu.android.presentation.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

// Activity
fun Context.showToast(msg: String) {
    //Todo 앱 진입시 빈 토스트 왜 뜨는지 알아야함
    if (msg.isNotEmpty()) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

// Fragment
fun Fragment.showToast(msg: String) {
    if (msg.isNotEmpty()) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}