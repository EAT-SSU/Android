package com.eatssu.android.ui.mypage.Inquire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.eatssu.android.BuildConfig
import com.eatssu.android.R
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.service.InquiresService
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityInquireBinding
import com.eatssu.android.ui.mypage.myreview.MyReviewListActivity
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.util.extension.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InquireActivity : BaseActivity<ActivityInquireBinding>(ActivityInquireBinding::inflate)  {
    private lateinit var inquiresService: InquiresService

    private var content = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "문의하기" // 툴바 제목 설정

        bindData()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun bindData(){
        content = binding.etReportComment.text.toString()

        binding.btnSendReport.setOnClickListener {
            signOut(content);
        }

    }

    private fun signOut(content: String) {
        inquiresService.inquireContent(content).enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>,
            ) {
                if (response.isSuccessful){
                    if (response.code() == 200) {
                        Log.d("InquireActivity", "onResponse 성공: 문의하기" + response.body().toString())

                    } else {
                        Log.d("InquireActivity", "onResponse 오류: 문의하기" + response.body().toString())
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("InquireActivity", "onFailure 에러: 문의하기" + t.message.toString())
            }
        })
    }
}