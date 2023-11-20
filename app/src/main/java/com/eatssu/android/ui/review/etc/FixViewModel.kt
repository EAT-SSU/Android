package com.eatssu.android.ui.review.etc

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.util.RetrofitImpl.retrofit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FixViewModel : ViewModel() {
    // LiveData to observe the result of the server communication
    val resultLiveData = MutableLiveData<String>()

    @SuppressLint("SuspiciousIndentation")
    fun postData(reviewId: Long, comment: String, mainGrade: Int, amountGrade: Int, tasteGrade: Int) {
        val service = retrofit.create(ReviewService::class.java)

        val reviewData = """
            {
                "mainGrade": $mainGrade,
                "amountGrade": $amountGrade,
                "tasteGrade": $tasteGrade,
                "content": "$comment"
            }
        """.trimIndent().toRequestBody("application/json".toMediaTypeOrNull())

            service.modifyReview(reviewId, reviewData)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                resultLiveData.value = "수정이 완료되었습니다."
                            } else {
                                resultLiveData.value = "수정이 실패하였습니다."
                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        resultLiveData.value = "수정이 실패하였습니다."
                    }
                })
        }
    }