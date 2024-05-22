package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.InquiryRequest
import kotlinx.coroutines.flow.Flow

interface InquiryRepository {
    suspend fun postInquiry(
        body: InquiryRequest,
    ): Flow<BaseResponse<Void>>

}

