package com.eatssu.android.domain.repository

import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant

interface PartnershipRepository {
    suspend fun getAllPartnerships(): List<Partnership>
    suspend fun getPartnershipById(partnershipId: Int): PartnershipRestaurant?
}