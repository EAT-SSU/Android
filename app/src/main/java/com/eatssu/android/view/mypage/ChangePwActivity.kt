package com.eatssu.android.view.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.eatssu.android.R
import com.eatssu.android.data.model.request.ChangePwRequestDto
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityChangePwBinding
import com.eatssu.android.base.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ChangePwActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePwBinding

    private var chPW: String = ""
    private var chPW2: String = ""

    private val pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePwBinding.inflate(layoutInflater)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_change_pw, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

        supportActionBar?.title = "비밀번호 변경"

        binding.etChPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                chPW = binding.etChPw.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.etChPw2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                chPW2 = binding.etChPw2.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.btnChPwDone.setOnClickListener{
            pwCheck()
            pwDoubleCheck()

            val userService =
                RetrofitImpl.retrofit.create(UserService::class.java)
            userService.changePw(ChangePwRequestDto(chPW)).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Log.d("post", "onResponse 성공: " + response.body().toString());

                        if (response.code() == 200) {
                            Toast.makeText(
                                this@ChangePwActivity, "비밀번호 변경에 성공했습니다.", Toast.LENGTH_SHORT
                            ).show()
                            finish()

                        } else {
                            Toast.makeText(
                                this@ChangePwActivity, "비밀번호 변경에 실패했습니다.\"", Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("post", "onFailure 에러: " + t.message.toString());
                }
            })
        }
    }

    //패스워드 정규성검사
    private fun pwCheck(): Boolean {
        val pattern2 = Pattern.compile(pwPattern) // 패턴 컴파일
        val matcher2 = pattern2.matcher(chPW)

        return if (!matcher2.find()) {
            Toast.makeText(this@ChangePwActivity, "비밀번호는 영문자과 숫자를 포함하여 8자 이상을 입력해주세요.", Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            true
        }
    }

    //패스워드 일치 검사 로직
    private fun pwDoubleCheck(): Boolean {
        return if (chPW == chPW2) {
            true
        } else {
            Toast.makeText(this@ChangePwActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

            false
        }
    }
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_change_pw
    }
}