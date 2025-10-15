package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.response.toDomain
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.model.orNull
import com.eatssu.android.data.service.PartnershipService
import com.eatssu.android.data.service.UserService
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import javax.inject.Inject

class PartnershipRepositoryImpl @Inject constructor(
    private val partnershipService: PartnershipService,
    private val userService: UserService,
) : PartnershipRepository {

    // 유저의 학과 상관없이 모든 제휴 정보 조회
    override suspend fun getAllPartnerships(): List<Partnership> =
        partnershipService.getAllPartnerships()
            .map { list -> list.map { it.toDomain() } }
            .orEmptyList()

    override suspend fun getPartnershipById(partnershipId: Int): PartnershipRestaurant? =
        partnershipService.getPartnershipById(partnershipId).map { it.toDomain() }.orNull()

    override suspend fun getUserCollegePartnerships(): List<Partnership> =
        userService.getUserDepartmentPartnerships().map { list -> list.map { it.toDomain() } }
            .orEmptyList()
}
