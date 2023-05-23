package com.eatssu.android.data.model.response

import com.google.gson.annotations.SerializedName

data class InfoResponse (
    @SerializedName("location")
    val location: String,

    @SerializedName("openHours")
    val openHours: List<Result>
) {
    data class Result(
            val dayType: String,
            val timepart: String,
            val time: String
    )
}
