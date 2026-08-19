package com.eatssu.android.domain.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant

interface PartnershipRepository {
    suspend fun getAllPartnerships(): List<Partnership>
    suspend fun getPartnershipById(partnershipId: Int): PartnershipRestaurant?
    suspend fun likePartnership(
        partnershipId: Int,
        wasLiked: Boolean? = null,
    ): ApiResult<Unit>
    suspend fun getUserCollegePartnerships(): List<Partnership>
    suspend fun getUserFavoritePartnerships(): List<Partnership>
}
