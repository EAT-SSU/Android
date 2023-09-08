package com.eatssu.android.view.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.eatssu.android.R
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.model.request.ChangeNicknameRequestDto
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityChangeNicknameBinding
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.RetrofitImpl.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeNicknameActivity : BaseActivity() {
    private lateinit var binding: ActivityChangeNicknameBinding

    private var chNick: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeNicknameBinding.inflate(layoutInflater)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_change_nickname, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

        supportActionBar?.title = "닉네임 설정"

        binding.etChNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                chNick = binding.etChNickname.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.btnChNicknameDone.setOnClickListener{

            val userService =
                retrofit.create(UserService::class.java)
            userService.changeNickname(ChangeNicknameRequestDto(chNick)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("post", "onResponse 성공: " + response.body().toString());

                        if (response.code() == 200) {
                            Toast.makeText(
                                this@ChangeNicknameActivity, "닉네임 변경에 성공했습니다.", Toast.LENGTH_SHORT
                            ).show()
                            MySharedPreferences.setUserName(this@ChangeNicknameActivity,chNick)
                            finish()
                        } else {
                            Toast.makeText(
                                this@ChangeNicknameActivity, "닉네임 변경에 실패했습니다.\"", Toast.LENGTH_SHORT
                            ).show()
                            Log.d("post", "onResponse 실패: " + response.body().toString());


                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("post", "onFailure 에러: " + t.message.toString());
                    Toast.makeText(
                        this@ChangeNicknameActivity, "닉네임 변경에 실패했습니다.\"", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_change_nickname
    }
}