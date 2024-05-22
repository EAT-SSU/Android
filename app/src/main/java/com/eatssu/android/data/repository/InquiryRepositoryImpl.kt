package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.InquiryRequest
import com.eatssu.android.data.service.InquiryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InquiryRepositoryImpl @Inject constructor(private val inquiryService: InquiryService) :
    InquiryRepository {

    override suspend fun postInquiry(body: InquiryRequest): Flow<BaseResponse<Void>> =
        flow {
            emit(inquiryService.inquireContent(body))
        }

}
