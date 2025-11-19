package com.eatssu.android.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class ModifyReviewRequest(
    @SerializedName("mainRating") var mainRating: Int? = null,
    @SerializedName("amountRating") var amountRating: Int? = null,
    @SerializedName("tasteRating") var tasteRating: Int? = null,
    @SerializedName("content") var content: String? = null,
)