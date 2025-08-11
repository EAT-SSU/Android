package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.response.PartnershipResponse
import com.eatssu.android.data.dto.response.PartnershipRestaurantResponse
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
    override suspend fun getAllPartnerships(): List<Partnership> {
        return partnershipService.getAllPartnerships()
            .result
            ?.map { it.toDomain() } ?: emptyList()
    }

    // 특정 식당 클릭 시 제휴 정보 조회
    override suspend fun getPartnershipById(partnershipId: Int): PartnershipRestaurant? {
        return partnershipService.getPartnershipById(partnershipId)
            .result
            ?.toDomain()
    }

    // 유저의 학과에 해당하는 제휴 정보 조회
    override suspend fun getUserCollegePartnerships(): List<Partnership> {
        return userService.getUserDepartmentPartnerships()
            .result
            ?.map { it.toDomain() } ?: emptyList()
    }


}

fun PartnershipResponse.toDomain(): Partnership =
    Partnership(
        storeName = storeName,
        longitude = longitude,
        latitude = latitude,
        restaurantType = restaurantType,
        partnershipInfos = partnershipInfos.map {
            Partnership.PartnershipInfo(
                id = it.id,
                partnershipType = it.partnershipType,
                collegeName = it.collegeName ?: "",
                departmentName = it.departmentName ?: "",
                likeCount = it.likeCount,
                isLiked = it.isLiked,
                description = it.description,
                startDate = it.startDate,
                endDate = it.endDate
            )
        }
    )

fun PartnershipRestaurantResponse.toDomain(): PartnershipRestaurant =
    PartnershipRestaurant(
        id = id,
        partnershipType = partnershipType,
        storeName = storeName,
        description = description,
        startDate = startDate,
        endDate = endDate,
        restaurantType = restaurantType,
        longitude = longitude,
        latitude = latitude,
        collegeName = collegeName,
        departmentName = departmentName ?: "",
        partnershipLikeCount = partnershipLikeCount,
        likedByUser = likedByUser
    )
