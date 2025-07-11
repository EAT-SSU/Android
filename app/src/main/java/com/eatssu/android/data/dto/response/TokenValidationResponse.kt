package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class TokenValidationResponse(
    @SerializedName("isValid")
    val isValid: Boolean
) 