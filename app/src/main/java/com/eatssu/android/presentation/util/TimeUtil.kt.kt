package com.eatssu.android.presentation.util

object TimeUtil {
    fun getTimeIndex(time: Int): Int {
        return when (time) {
            in 0..9 -> 0 //아침
            in 10..15 -> 1 //점심
            in 16..24 -> 2 //저녁
            else -> 3
        }
    }
}
