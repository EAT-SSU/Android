package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.common.enums.StoreType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartnershipRestaurantResponse(
    @SerialName("id")
    val id: Int?,
    @SerialName("partnershipType")
    val partnershipType: String?,
    @SerialName("storeName")
    val storeName: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("startDate")
    val startDate: String?,
    @SerialName("endDate")
    val endDate: String?,
    @SerialName("restaurantType")
    val restaurantType: StoreType?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("collegeName")
    val collegeName: String?,
    @SerialName("departmentName")
    val departmentName: String?,
    @SerialName("partnershipLikeCount")
    val partnershipLikeCount: Int?,
    @SerialName("likedByUser")
    val likedByUser: Boolean?,
)

fun PartnershipRestaurantResponse.toDomain(): PartnershipRestaurant =
    PartnershipRestaurant(
        id = id ?: -1,
        partnershipType = partnershipType ?: "",
        storeName = storeName ?: "",
        description = description ?: "",
        startDate = startDate ?: "",
        endDate = endDate ?: "",
        restaurantType = restaurantType ?: StoreType.RESTAURANT,
        longitude = longitude ?: 126.95661313346206,
        latitude = latitude ?: 37.49517278813046,
        collegeName = collegeName ?: "",
        departmentName = departmentName ?: "",
        partnershipLikeCount = partnershipLikeCount ?: 0,
        likedByUser = likedByUser ?: false
    )
