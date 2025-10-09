package com.eatssu.android.di.network

import com.eatssu.android.data.dto.response.BaseResponse
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class BaseResponseConverter : Converter.Factory() {
    private val gson = Gson()

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return BaseEntityResponseBodyConverter(gson, type)
    }

    private class BaseEntityResponseBodyConverter(
        private val gson: Gson,
        private val type: Type
    ) : Converter<ResponseBody, BaseResponse<Any>> {
        override fun convert(value: ResponseBody): BaseResponse<Any> = value.use { body ->
//            val baseEntity = if (type == BaseResponse::class.java) {
//                val jsonString = body.string()
//
//                val jsonString = Json.parseToJsonElement(jsonString)
//                if (jsonString is JsonObject) {
//                    BaseResponse(
//                        isSuccess = jsonString.isSuccess,
//                        data = Unit,
//                        error = jsonString.error
//                    )
//                } else {
//                    throw IllegalArgumentException("The responseBody is not JsonObject Type. $jsonString")
//                }
//            } else {
//                gson.fromJson<BaseResponse<Any>>(body.charStream(), type)
//            }
            gson.fromJson(body.charStream(), type)
        }


    }
}