package com.eatssu.android.di.network

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.BaseResponse
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapterFactory(private val json: Json) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) == ApiResult::class.java) {
            throw IllegalStateException("함수가 suspend로 선언 되어 있지 않습니다: ${annotations.joinToString { it.toString() }}")
        }

        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) {
            "Return 타입은 ApiResult<T> 형태여야 합니다: $returnType"
        }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResult::class.java) return null
        check(responseType is ParameterizedType) {
            "Return 타입은 ApiResult<T> 형태여야 합니다: $returnType"
        }

        val successType = getParameterUpperBound(0, responseType)
        return createCallAdapter(successType)
    }

    fun createCallAdapter(successType: Type): ApiResultCallAdapter<Any> {
        val baseResponseType = object : ParameterizedType {
            override fun getRawType(): Type = BaseResponse::class.java
            override fun getActualTypeArguments(): Array<Type> = arrayOf(successType)
            override fun getOwnerType(): Type? = null
        }
        return ApiResultCallAdapter(baseResponseType, successType, json)
    }
}
