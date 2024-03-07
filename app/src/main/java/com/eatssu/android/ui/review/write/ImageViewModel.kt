package com.eatssu.android.ui.review.write

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eatssu.android.App
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.data.service.ImageService
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ImageViewModel(
    private val imageService: ImageService,
) : ViewModel() {

    private val _imagePath = MutableLiveData<String>()
    val imagePath: LiveData<String> get() = _imagePath

    private var _imageString = MutableLiveData<String>("")
    val imageString: LiveData<String> get() = _imageString

    private suspend fun compressImage(imageString: String?): MultipartBody.Part? {

        if (imageString != null) {
            val file = File(imageString)
            val compressedFile =
                Compressor.compress(App.appContext, file) { quality(80) }
            val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())

            return MultipartBody.Part.createFormData(
                "multipartFileList",
                compressedFile.name,
                requestFile
            )
        }
        return null
    }

    suspend fun getImageString(imageString: String?) {
        val compressImageString = compressImage(imageString)

        if (compressImageString != null) { //null일 때는 할 필요가 없음

            imageService.getImageUrl(compressImageString).enqueue(
                object : Callback<BaseResponse<ImageResponse>> {
                    override fun onResponse(
                        call: Call<BaseResponse<ImageResponse>>,
                        response: Response<BaseResponse<ImageResponse>>,
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            _imageString.value = response.body()?.result?.url.toString()
                            Log.d("ReviewWriteRateActivity", _imageString.value.toString())


                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("ReviewWriteRateActivity", "onResponse 리뷰 작성 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<ImageResponse>>,
                        t: Throwable,
                    ) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d(
                            "ReviewWriteRateActivity",
                            "onFailure 에러: " + t.message.toString()
                        )
                    }
                })

        }
    }
}


