package com.eatssu.android.domain.usecase.user

import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import javax.inject.Inject

class GetPartnershipDetailUseCase @Inject constructor() {
    operator fun invoke(
        partnerships: List<Partnership>,
        storeName: String,
        partnershipId: Int? = null
    ): PartnershipRestaurant? {
        val matched = partnerships.find { it.storeName == storeName } ?: return null
        val targetInfo = partnershipId?.let { id ->
            matched.partnershipInfos.find { it.id == id }
        } ?: matched.partnershipInfos.firstOrNull()

        return targetInfo?.let { info ->
            PartnershipRestaurant(
                id = info.id,
                partnershipType = info.partnershipType,
                storeName = matched.storeName,
                description = info.description,
                startDate = info.startDate,
                endDate = info.endDate,
                storeType = matched.restaurantType,
                longitude = matched.longitude,
                latitude = matched.latitude,
                collegeName = info.collegeName,
                departmentName = info.departmentName,
                partnershipLikeCount = info.likeCount,
                likedByUser = info.isLiked,
                naverMapUrl = matched.naverMapUrl,
                kakaoMapUrl = matched.kakaoMapUrl,
            )
        }
    }
}
