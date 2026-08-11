package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.model.isSuccess
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.model.orNull
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.PartnershipService
import com.eatssu.android.data.remote.service.UserService
import com.eatssu.android.domain.model.Partnership
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.domain.repository.PartnershipRepository
import javax.inject.Inject

class PartnershipRepositoryImpl @Inject constructor(
    private val partnershipService: PartnershipService,
    private val userService: UserService,
    private val favoritePartnershipDataStore: FavoritePartnershipDataStore,
) : PartnershipRepository {

    // 유저의 학과 상관없이 모든 제휴 정보 조회
    override suspend fun getAllPartnerships(): List<Partnership> =
        partnershipService.getAllPartnerships()
            .map { list -> list.map { it.toDomain() } }
            .orEmptyList()

    // 특정 식당 클릭 시 제휴 정보 조회
    override suspend fun getPartnershipById(partnershipId: Int): PartnershipRestaurant? =
        partnershipService.getPartnershipById(partnershipId).map { it.toDomain() }.orNull()

    // 특정 식당 클릭 시 제휴 정보 조회
    override suspend fun likePartnership(
        partnershipId: Int,
        wasLiked: Boolean?,
    ): ApiResult<Unit> {
        val result = partnershipService.likePartnership(partnershipId)
        if (result.isSuccess() && wasLiked != null) {
            if (wasLiked) {
                favoritePartnershipDataStore.markUnliked(partnershipId)
            } else {
                favoritePartnershipDataStore.markLiked(partnershipId)
            }
        }
        return result
    }


    // 유저의 학과에 해당하는 제휴 정보 조회
    override suspend fun getUserCollegePartnerships(): List<Partnership> =
        userService.getUserDepartmentPartnerships().map { list -> list.map { it.toDomain() } }
            .orEmptyList()

    // 서버에서 사용자가 찜한 제휴만 조회한다.
    override suspend fun getUserFavoritePartnerships(): List<Partnership> =
        userService.getUserFavoritePartnerships().map { list -> list.map { it.toDomain() } }
            .orEmptyList()
}
