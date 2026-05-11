package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Partnership
import com.eatssu.common.enums.PeriodType
import com.eatssu.common.enums.StoreType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartnershipResponse(
    @SerialName("storeName")
    val storeName: String? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("restaurantType")
    val restaurantType: StoreType? = null,
    @SerialName("partnershipInfos")
    val partnershipInfos: List<PartnershipInfo> = emptyList()
){
    @Serializable
    data class PartnershipInfo(
        @SerialName("id")
        val id: Int? = null,
        @SerialName("partnershipType")
        val partnershipType: String? = null,
        @SerialName("collegeName")
        val collegeName: String? = null,
        @SerialName("departmentName")
        val departmentName: String? = null,
        @SerialName("likeCount")
        val likeCount: Int? = null,
        @SerialName("isLiked")
        val isLiked: Boolean? = null,
        @SerialName("description")
        val description: String? = null,
        @SerialName("startDate")
        val startDate: String? = null,
        @SerialName("endDate")
        val endDate: String? = null,
        @SerialName("periodType")
        val periodType: PeriodType = PeriodType.NORMAL
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
                partnershipType = it.partnershipType ?: "",
                collegeName = it.collegeName ?: "",
                departmentName = it.departmentName ?: "",
                likeCount = it.likeCount ?: 0,
                isLiked = it.isLiked ?: false,
                description = it.description ?: "",
                startDate = it.startDate ?: "",
                endDate = it.endDate ?: "",
                periodType = it.periodType
            )
        }
    )
