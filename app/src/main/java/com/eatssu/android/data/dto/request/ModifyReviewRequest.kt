package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class ModifyReviewRequest(
    @SerializedName("mainRate") var mainRate: Int? = null,
    @SerializedName("amountRate") var amountRate: Int? = null,
    @SerializedName("tasteRate") var tasteRate: Int? = null,
    @SerializedName("content") var content: String? = null,
)