package com.eatssu.android.di.network

import com.eatssu.android.data.model.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) {
            "Return 타입은 ApiResult<T> 형태여야 합니다."
        }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResult::class.java) return null
        check(responseType is ParameterizedType) {
            "Return 타입은 ApiResult<T> 형태여야 합니다."
        }

        val bodyType = getParameterUpperBound(0, responseType)
        Timber.d("ApiResultCallAdapterFactory - bodyType: $bodyType")
        return ApiResultCallAdapter<Any>(bodyType)
    }
}

