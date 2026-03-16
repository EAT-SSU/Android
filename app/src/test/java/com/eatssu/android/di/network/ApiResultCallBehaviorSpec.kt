package com.eatssu.android.di.network

import app.cash.turbine.test
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.BaseResponse
import com.eatssu.android.presentation.base.NetworkErrorEventBus
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.serialization.json.Json
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

private class FakeBaseResponseCall<T : Any>(
    private val requestValue: Request = Request.Builder().url("https://example.com").build(),
    private val enqueueBlock: (Callback<BaseResponse<T>>) -> Unit,
) : Call<BaseResponse<T>> {
    private var executed = false
    private var canceled = false

    override fun enqueue(callback: Callback<BaseResponse<T>>) {
        executed = true
        enqueueBlock(callback)
    }

    override fun isExecuted(): Boolean = executed
    override fun clone(): Call<BaseResponse<T>> = FakeBaseResponseCall(requestValue, enqueueBlock)
    override fun isCanceled(): Boolean = canceled
    override fun cancel() {
        canceled = true
    }

    override fun execute(): Response<BaseResponse<T>> {
        throw UnsupportedOperationException("Fake call supports only enqueue")
    }

    override fun request(): Request = requestValue
    override fun timeout(): Timeout = Timeout.NONE
}

private fun <T : Any> ApiResultCall<T>.enqueueAndGet(): ApiResult<T> {
    var result: ApiResult<T>? = null
    enqueue(
        object : Callback<ApiResult<T>> {
            override fun onResponse(call: Call<ApiResult<T>>, response: Response<ApiResult<T>>) {
                result = response.body()
            }

            override fun onFailure(call: Call<ApiResult<T>>, t: Throwable) = Unit
        }
    )
    return result ?: error("ApiResult not emitted")
}

class ApiResultCallBehaviorSpec : AppBehaviorSpec({

    given("ApiResultCall") {
        val json = Json { ignoreUnknownKeys = true }

        `when`("HTTP 에러 + BaseResponse 에러바디 파싱 성공") {
            val errorJson = """{"isSuccess":false,"code":1234,"message":"bad request"}"""
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                it.onResponse(
                    retrofitCall,
                    Response.error(400, errorJson.toResponseBody())
                )
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("파싱된 code/message로 Failure를 반환한다") {
                val result = apiResultCall.enqueueAndGet() as ApiResult.Failure
                result.responseCode shouldBe 1234
                result.message shouldBe "bad request"
            }
        }

        `when`("HTTP 에러 + BaseResponse 파싱은 되지만 code가 없으면") {
            val errorJson = """{"isSuccess":false,"message":"bad request"}"""
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                it.onResponse(
                    retrofitCall,
                    Response.error(400, errorJson.toResponseBody())
                )
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("HTTP code와 raw 에러 문자열로 Failure를 반환한다") {
                val result = apiResultCall.enqueueAndGet() as ApiResult.Failure
                result.responseCode shouldBe 400
                result.message shouldBe errorJson
            }
        }

        `when`("HTTP 에러 + 에러바디 파싱 실패") {
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                it.onResponse(
                    retrofitCall,
                    Response.error(500, "not-json".toResponseBody())
                )
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("HTTP code와 raw 에러 문자열로 Failure를 반환한다") {
                val result = apiResultCall.enqueueAndGet() as ApiResult.Failure
                result.responseCode shouldBe 500
                result.message shouldBe "not-json"
            }
        }

        `when`("HTTP 성공이지만 body가 null이면") {
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                @Suppress("UNCHECKED_CAST")
                it.onResponse(
                    retrofitCall,
                    Response.success(null) as Response<BaseResponse<String>>
                )
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("UnknownError를 반환한다") {
                val result = apiResultCall.enqueueAndGet()
                (result is ApiResult.UnknownError) shouldBe true
            }
        }

        `when`("API 레벨에서 isSuccess=false이면") {
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                it.onResponse(
                    retrofitCall,
                    Response.success(BaseResponse(isSuccess = false, code = 2001, message = "api fail"))
                )
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("Failure(code,message)를 반환한다") {
                val result = apiResultCall.enqueueAndGet() as ApiResult.Failure
                result.responseCode shouldBe 2001
                result.message shouldBe "api fail"
            }
        }

        `when`("responseType이 Unit이면") {
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<Unit>>>(relaxed = true)
                it.onResponse(
                    retrofitCall,
                    Response.success(BaseResponse<Unit>(isSuccess = true, code = 0, message = "ok", result = null))
                )
            }
            val apiResultCall = ApiResultCall(origin, Unit::class.java as Type, json)

            then("Success(Unit)을 반환한다") {
                apiResultCall.enqueueAndGet() shouldBe ApiResult.Success(Unit)
            }
        }

        `when`("API 성공인데 result가 null이고 Unit이 아니면") {
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                it.onResponse(
                    retrofitCall,
                    Response.success(BaseResponse(isSuccess = true, code = 0, message = "ok", result = null))
                )
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("UnknownError를 반환한다") {
                val result = apiResultCall.enqueueAndGet()
                (result is ApiResult.UnknownError) shouldBe true
            }
        }

        `when`("API 성공 + result 존재") {
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                it.onResponse(
                    retrofitCall,
                    Response.success(BaseResponse(isSuccess = true, code = 0, message = "ok", result = "payload"))
                )
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("Success(result)를 반환한다") {
                apiResultCall.enqueueAndGet() shouldBe ApiResult.Success("payload")
            }
        }

        `when`("enqueue onFailure에서 IOException이 발생하면") {
            val io = IOException("offline")
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                it.onFailure(retrofitCall, io)
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("NetworkError를 반환하고 NetworkErrorEventBus를 발행한다") {
                NetworkErrorEventBus.networkError.test {
                    val result = apiResultCall.enqueueAndGet() as ApiResult.NetworkError
                    result.exception shouldBe io
                    awaitItem() shouldBe Unit
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        `when`("enqueue onFailure에서 기타 예외가 발생하면") {
            val error = IllegalStateException("boom")
            val origin = FakeBaseResponseCall {
                val retrofitCall = mockk<Call<BaseResponse<String>>>(relaxed = true)
                it.onFailure(retrofitCall, error)
            }
            val apiResultCall = ApiResultCall(origin, String::class.java, json)

            then("UnknownError를 반환한다") {
                val result = apiResultCall.enqueueAndGet() as ApiResult.UnknownError
                result.exception shouldBe error
            }
        }
    }
})
