package com.eatssu.android.presentation.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

// Activity
fun Context.showToast(msg: String) {
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