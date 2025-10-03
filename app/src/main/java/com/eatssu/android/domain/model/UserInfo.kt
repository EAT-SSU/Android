package com.eatssu.android.domain.model

data class UserInfo(
    val nickname: String,
    val userDepartment: Department?,
    val userCollege: College?,
)