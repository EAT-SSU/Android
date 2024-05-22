package com.eatssu.android.data.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.InquiryRequest
import com.eatssu.android.data.repository.InquiryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostInquiryUseCase @Inject constructor(
    private val inquiryRepository: InquiryRepository,
) {
    suspend operator fun invoke(body: InquiryRequest): Flow<BaseResponse<Void>> =
        inquiryRepository.postInquiry(body)

}