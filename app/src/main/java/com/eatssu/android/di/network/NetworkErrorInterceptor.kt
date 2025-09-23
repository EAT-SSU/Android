package com.eatssu.android.di.network

import android.content.Context
import android.content.Intent
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.presentation.error.ErrorActivity
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject


/**
 * 네트워크 오류를 처리하는 인터셉터
 * IOException(SocketTimeoutException, UnknownHostException) 발생 시 AlertDialog를 띄우는 ErrorActivity로 이동
 */
class NetworkErrorInterceptor @Inject constructor(
    private val context: Context,
) : Interceptor {

    companion object {
        private val gson = Gson()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        try {
            return chain.proceed(request)
        } catch (e: IOException) {
            val intent = Intent(context, ErrorActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra("message", "서버 통신에 실패했습니다. 잠시 후 다시 시도해 주세요.")
            }
            context.startActivity(intent)

            val baseResponse = BaseResponse<Void>(
                isSuccess = false,
                code = 500, // 서버 처리 오류인지 통신 불가인지 구분
                message = "서버 통신 실패",
            )
            val json = gson.toJson(baseResponse)
            val responseBody = json.toResponseBody("application/json".toMediaTypeOrNull())

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200) // HTTP 응답 코드는 200으로 해야 Retrofit에서 에러로 처리하지 않음
                .message("서버 통신 실패")
                .body(responseBody)
                .build()
        }
    }
}
