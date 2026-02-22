package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.common.enums.StoreType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartnershipRestaurantResponse(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("partnershipType")
    val partnershipType: String? = null,
    @SerialName("storeName")
    val storeName: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("startDate")
    val startDate: String? = null,
    @SerialName("endDate")
    val endDate: String? = null,
    @SerialName("restaurantType")
    val restaurantType: StoreType? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("collegeName")
    val collegeName: String? = null,
    @SerialName("departmentName")
    val departmentName: String? = null,
    @SerialName("partnershipLikeCount")
    val partnershipLikeCount: Int? = null,
    @SerialName("likedByUser")
    val likedByUser: Boolean? = null,
)

fun PartnershipRestaurantResponse.toDomain(): PartnershipRestaurant =
    PartnershipRestaurant(
        id = id ?: -1,
        partnershipType = partnershipType ?: "",
        storeName = storeName ?: "",
        description = description ?: "",
        startDate = startDate ?: "",
        endDate = endDate ?: "",
        storeType = restaurantType ?: StoreType.RESTAURANT,
        longitude = longitude ?: 126.95661313346206,
        latitude = latitude ?: 37.49517278813046,
        collegeName = collegeName ?: "",
        departmentName = departmentName ?: "",
        partnershipLikeCount = partnershipLikeCount ?: 0,
        likedByUser = likedByUser ?: false
    )
