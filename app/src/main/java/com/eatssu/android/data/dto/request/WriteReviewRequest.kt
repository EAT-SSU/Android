package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class WriteReviewRequest(

    @SerializedName("mainRating") var mainRating: Int? = null,
    @SerializedName("amountRating") var amountRating: Int? = null,
    @SerializedName("tasteRating") var tasteRating: Int? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("imageUrl") var imageUrl: String? = null,

    )