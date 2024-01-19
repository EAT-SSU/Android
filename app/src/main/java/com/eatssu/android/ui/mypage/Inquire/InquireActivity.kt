package com.eatssu.android.ui.mypage.Inquire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.eatssu.android.BuildConfig
import com.eatssu.android.R
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.service.InquiresService
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityInquireBinding
import com.eatssu.android.ui.main.MainActivity
import com.eatssu.android.ui.mypage.myreview.MyReviewListActivity
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.util.MySharedPreferences
import com.eatssu.android.util.extension.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class InquireActivity : BaseActivity<ActivityInquireBinding>(ActivityInquireBinding::inflate)  {
    private lateinit var inquiresService: InquiresService

    private var content = ""
    private var email = ""

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

        // 카카오 로그인으로 받아온 이메일 정보 가져오기
        val userEmail = MySharedPreferences.getUserEmail(this)

        // EditText에 이메일 정보 설정
        binding.etEmail.setText(userEmail)
        // EditText를 편집 가능하게 만들기
        binding.etEmail.isEnabled = true

        binding.btnSendReport.setOnClickListener {
            inquireContent(content);
        }
    }

    private fun inquireContent(content: String) {
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