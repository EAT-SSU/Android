package com.eatssu.android.util.extension


fun getCompleteWordByJongsung(name: String, firstValue: String, secondValue: String): String {
    val lastName = name.last()

    // 한글의 제일 처음과 끝의 범위밖일 경우는 오류
    if (lastName < 0xAC00.toChar() || lastName > 0xD7A3.toChar()) {
        return firstValue //영어 일때는 무조건 을
//        return name
    }

    val selectedValue = if ((lastName.code - 0xAC00) % 28 > 0) firstValue else secondValue

//    return name + selectedValue
    return selectedValue
}