package com.eatssu.android.di.network

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.BaseResponse
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import retrofit2.Call
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapterBehaviorSpec : AppBehaviorSpec({

    given("ApiResultCallAdapter") {
        fun parameterizedType(rawType: Type, vararg args: Type): ParameterizedType =
            object : ParameterizedType {
                override fun getRawType(): Type = rawType
                override fun getActualTypeArguments(): Array<Type> = arrayOf(*args)
                override fun getOwnerType(): Type? = null
            }

        val baseResponseType = parameterizedType(BaseResponse::class.java, String::class.java)
        val adapter = ApiResultCallAdapter<String>(
            baseResponseType = baseResponseType,
            dataType = String::class.java,
        )

        `when`("responseType을 조회하면") {
            then("생성 시 전달된 baseResponseType을 그대로 반환한다") {
                adapter.responseType() shouldBe baseResponseType
            }
        }

        `when`("Call<BaseResponse<T>>를 adapt하면") {
            val call = mockk<Call<BaseResponse<String>>>(relaxed = true)

            then("ApiResultCall로 감싸서 반환한다") {
                val adapted = adapter.adapt(call)

                (adapted is ApiResultCall<*>) shouldBe true
                (adapted as Call<ApiResult<String>>).request() shouldBe call.request()
            }
        }
    }
})
