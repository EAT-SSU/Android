package com.eatssu.android.di.network

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.BaseResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ApiResultCallAdapter<T : Any>(
    private val baseResponseType: Type,
    private val dataType: Type
) : CallAdapter<BaseResponse<T>, Call<ApiResult<T>>> {

    override fun responseType(): Type = baseResponseType

    override fun adapt(call: Call<BaseResponse<T>>): Call<ApiResult<T>> {
        return ApiResultCall(call, dataType)
    }
}

