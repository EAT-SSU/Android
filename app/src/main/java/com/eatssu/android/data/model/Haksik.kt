package com.eatssu.android.data.model

import com.google.gson.annotations.SerializedName;

public class Haksik(
    @SerializedName("menu")
    val menu: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("rate")
    val rate: Double
    )
