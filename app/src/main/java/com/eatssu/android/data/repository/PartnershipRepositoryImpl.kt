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

    override suspend fun getAllPartnerships(): List<Partnership> {
        return partnershipService.getAllPartnerships()
            .result
            ?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getPartnershipById(partnershipId: Int): PartnershipRestaurant? {
        return partnershipService.getPartnershipById(partnershipId)
            .result
            ?.toDomain()
    }

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
