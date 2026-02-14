package com.eatssu.android.di.network

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.BaseResponse
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import retrofit2.Call
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapterFactoryBehaviorSpec : AppBehaviorSpec({

    given("ApiResultCallAdapterFactory") {
        val factory = ApiResultCallAdapterFactory()
        val retrofit = mockk<Retrofit>()

        fun parameterizedType(rawType: Type, vararg args: Type): ParameterizedType =
            object : ParameterizedType {
                override fun getRawType(): Type = rawType
                override fun getActualTypeArguments(): Array<Type> = arrayOf(*args)
                override fun getOwnerType(): Type? = null
            }

        `when`("return type이 ApiResult 자체면") {
            then("suspend 선언 누락 예외를 던진다") {
                shouldThrow<IllegalStateException> {
                    factory.get(
                        parameterizedType(ApiResult::class.java, String::class.java),
                        emptyArray(),
                        retrofit,
                    )
                }
            }
        }

        `when`("return type raw type이 Call이 아니면") {
            then("adapter를 만들지 않고 null을 반환한다") {
                factory.get(String::class.java, emptyArray(), retrofit).shouldBeNull()
            }
        }

        `when`("return type이 raw Call이면") {
            then("ApiResult<T> 형태가 아니라는 예외를 던진다") {
                shouldThrow<IllegalStateException> {
                    factory.get(Call::class.java, emptyArray(), retrofit)
                }
            }
        }

        `when`("Call 내부 타입이 ApiResult가 아니면") {
            then("adapter를 만들지 않고 null을 반환한다") {
                val returnType = parameterizedType(Call::class.java, String::class.java)
                factory.get(returnType, emptyArray(), retrofit).shouldBeNull()
            }
        }

        `when`("Call<ApiResult>처럼 success 타입 파라미터가 빠지면") {
            then("ApiResult<T> 형태가 아니라는 예외를 던진다") {
                val returnType = parameterizedType(
                    Call::class.java,
                    ApiResult::class.java,
                )

                shouldThrow<IllegalStateException> {
                    factory.get(returnType, emptyArray(), retrofit)
                }
            }
        }

        `when`("Call<ApiResult<String>>이면") {
            then("ApiResultCallAdapter를 반환하고 BaseResponse<String> responseType을 구성한다") {
                val returnType = parameterizedType(
                    Call::class.java,
                    parameterizedType(ApiResult::class.java, String::class.java),
                )

                val adapter = factory.get(returnType, emptyArray(), retrofit)
                (adapter is ApiResultCallAdapter<*>) shouldBe true

                val responseType = (adapter as ApiResultCallAdapter<Any>).responseType() as ParameterizedType
                responseType.rawType shouldBe BaseResponse::class.java
                responseType.actualTypeArguments.single() shouldBe String::class.java
            }
        }

        `when`("createCallAdapter를 직접 호출하면") {
            then("지정한 successType으로 responseType을 생성한다") {
                val adapter = factory.createCallAdapter(Int::class.java)
                val responseType = adapter.responseType() as ParameterizedType

                responseType.rawType shouldBe BaseResponse::class.java
                responseType.actualTypeArguments.single() shouldBe Int::class.java
            }
        }
    }
})
