package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody


interface ImageRepository {

    suspend fun getImageString(
        image: MultipartBody.Part,
    ): Flow<BaseResponse<ImageResponse>>

}