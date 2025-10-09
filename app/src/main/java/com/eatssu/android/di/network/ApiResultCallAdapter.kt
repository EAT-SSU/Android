package com.eatssu.android.di.network

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.model.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ApiResultCallAdapter<T : Any>(
    private val successType: Type,
) : CallAdapter<BaseResponse<T>, Call<ApiResult<T>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<BaseResponse<T>>): Call<ApiResult<T>> {
        return ApiResultCall(call, successType)
    }
}

