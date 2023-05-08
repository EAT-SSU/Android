package com.eatssu.android.data.model.response

data class MenuBaseResponse (
    val menuInforesult : List<Result>
) {
    data class Result(
        val name: String,
        val price: String,
        val grade: Float
    )
}
