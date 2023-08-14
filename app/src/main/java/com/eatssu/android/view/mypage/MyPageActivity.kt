package com.eatssu.android.view.mypage

import RetrofitImpl
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.eatssu.android.R
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.model.response.GetMyInfoResponseDto
import com.eatssu.android.data.service.MyPageService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageActivity : BaseActivity() {
    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_my_page, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

        supportActionBar?.title = "마이페이지"

        /*binding.tvNicknameChangeBtn.setOnClickListener {
            val intent = Intent(this, ChangeNicknameActivity::class.java)
            startActivity(intent)
            finish()
        }*/

        binding.clNickname.setOnClickListener{
            val intent = Intent(this, ChangeNicknameActivity::class.java)
            startActivity(intent)
            //finish()
        }

        binding.clChPw.setOnClickListener{
            val intent = Intent(this, ChangePwActivity::class.java)
            startActivity(intent)
//            finish()
        }

        binding.clReview.setOnClickListener{
            val intent = Intent(this, MyReviewListActivity::class.java)
            startActivity(intent)
//            finish()
        }

        binding.tvLogout.setOnClickListener{

            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃",
                    DialogInterface.OnClickListener { dialog, id ->
                        //로그아웃
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }

        binding.tvSignout.setOnClickListener{

            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(this)
            builder.setTitle("탈퇴하기")
                .setMessage("탈퇴 하시겠습니까?")
                .setPositiveButton("탈퇴하기",
                    DialogInterface.OnClickListener { dialog, id ->
                        //탈퇴처리
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }

        var email = MySharedPreferences.getUserEmail(this).split("@")
        var emailsplit = email[1].split(".")
        Log.d("myemail", email.toString())
        loadMyInfo(emailsplit[1])
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_my_page
    }

    private fun loadMyInfo(emailtype : String) {
        val myInfoService = RetrofitImpl.retrofit.create(MyPageService::class.java)
        myInfoService.getMyInfo().enqueue(object:
            Callback<GetMyInfoResponseDto> {
            override fun onResponse(
                call: Call<GetMyInfoResponseDto>,
                response: Response<GetMyInfoResponseDto>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("myinfopost", "onResponse 성공: " + response.body().toString());

                    val body = response.body()
                    body?.let {
                        binding.tvNickname.text = it.nickname
                        if (emailtype == "kakao" || emailtype == "naver")
                            binding.tvEmail.text = "카카오"
                        else
                            binding.tvEmail.text = it.accountForm
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("myinfopost", "onResponse 실패 + ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GetMyInfoResponseDto>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("myinfopost", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}