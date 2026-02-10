package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Partnership
import com.eatssu.common.enums.StoreType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartnershipResponse(
    @SerialName("storeName")
    val storeName: String?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("restaurantType")
    val restaurantType: StoreType?,
    @SerialName("partnershipInfos")
    val partnershipInfos: List<PartnershipInfo>
){
    @Serializable
    data class PartnershipInfo(
        @SerialName("id")
        val id: Int?,
        @SerialName("partnershipType")
        val partnershipType: StoreType?,
        @SerialName("collegeName")
        val collegeName: String?,
        @SerialName("departmentName")
        val departmentName: String?,
        @SerialName("likeCount")
        val likeCount: Int?,
        @SerialName("isLiked")
        val isLiked: Boolean?,
        @SerialName("description")
        val description: String?,
        @SerialName("startDate")
        val startDate: String?,
        @SerialName("endDate")
        val endDate: String?
    )
}

fun PartnershipResponse.toDomain(): Partnership =
    Partnership(
        storeName = storeName ?: "",
        longitude = longitude ?: 126.95661313346206,
        latitude = latitude ?: 37.49517278813046,
        restaurantType = restaurantType ?: StoreType.RESTAURANT,
        partnershipInfos = partnershipInfos.map {
            Partnership.PartnershipInfo(
                id = it.id ?: -1,
                partnershipType = it.partnershipType ?: StoreType.RESTAURANT,
                collegeName = it.collegeName ?: "",
                departmentName = it.departmentName ?: "",
                likeCount = it.likeCount ?: 0,
                isLiked = it.isLiked ?: false,
                description = it.description ?: "",
                startDate = it.startDate ?: "",
                endDate = it.endDate ?: ""
            )
        }
    )
