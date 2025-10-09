package com.eatssu.android.di.network

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.model.ApiResult
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
class ApiResultCall<T : Any>(
    private val call: Call<BaseResponse<T>>,
    private val responseType: Type
) : Call<ApiResult<T>> {

    override fun enqueue(callback: Callback<ApiResult<T>>) {
        call.enqueue(object : Callback<BaseResponse<T>> {
            override fun onResponse(
                call: Call<BaseResponse<T>>,
                response: Response<BaseResponse<T>>
            ) {
                callback.onResponse(
                    this@ApiResultCall,
                    Response.success(response.toApiResult()) as Response<ApiResult<T>>
                )
            }

            override fun onFailure(call: Call<BaseResponse<T>>, error: Throwable) {
                Timber.e(error, "ApiResultCall - onFailure called")
                val response = when (error) {
                    is IOException -> ApiResult.NetworkError(error)
                    else -> ApiResult.UnknownError(error)
                }
                callback.onResponse(
                    this@ApiResultCall,
                    Response.success(response) as Response<ApiResult<T>>
                )
            }
        })
    }

    private fun Response<BaseResponse<T>>.toApiResult(): ApiResult<out T> {
        // HTTP Response code가 200 ~ 300대가 아닌 경우 (ex. 400, 500)
        if (!isSuccessful) {
            val responseCode = code()
            val errorMessage = errorBody()?.string()

            Timber.d("ApiResultCall - HTTP Response is not successful: $responseCode - $errorMessage")
            return ApiResult.Failure(
                responseCode,
                errorMessage
            )
        }

        val body = body()

        // 오류가 발생해도 프로토콜상 Body는 존재해야 함
        if (body == null) {
            Timber.d("ApiResultCall - Response body is null")
            return ApiResult.UnknownError(
                IllegalStateException("Response Body가 존재하지 않습니다.")
            )
        }

        if (body.isSuccess == false) {
            Timber.d("ApiResultCall - API indicates failure: ${body.code} - ${body.message}")
            return ApiResult.Failure(
                body.code ?: -1,
                body.message
            )
        }

        if (responseType == Unit::class.java) {
            Timber.d("ApiResultCall - Response type is Unit, returning Success with Unit")
            return ApiResult.Success(Unit as T)
        }

        val result = body.result
        if (result == null) {
            Timber.d("ApiResultCall - Result is null in successful response")
            return ApiResult.UnknownError(
                IllegalStateException("Result가 존재하지 않습니다.")
            )
        }

        Timber.d("ApiResultCall - Successful API call, returning Success with $result")
        return ApiResult.Success(result)
    }


    override fun isExecuted(): Boolean = call.isExecuted

    override fun clone(): Call<ApiResult<T>> = ApiResultCall(call.clone(), responseType)

    override fun isCanceled(): Boolean = call.isCanceled

    override fun cancel() = call.cancel()

    override fun execute(): Response<ApiResult<T>> {
        throw UnsupportedOperationException("EatssuCall doesn't support execute")
    }

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}

