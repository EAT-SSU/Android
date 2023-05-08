package com.eatssu.android.ui.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.eatssu.android.R
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.request.ChangeNickname
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityChangeNicknameBinding
import com.eatssu.android.ui.BaseActivity
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

        supportActionBar?.title = "닉네임 변경"

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
                RetrofitImpl.getApiClient().create(UserService::class.java)
            userService.changeNickname(ChangeNickname(chNick)).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Log.d("post", "onResponse 성공: " + response.body().toString());

                        if (response.code() == 200) {
                            Toast.makeText(
                                this@ChangeNicknameActivity, "닉네임 변경에 성공했습니다.", Toast.LENGTH_SHORT
                            ).show()
                            finish()

                        } else {
                            Toast.makeText(
                                this@ChangeNicknameActivity, "닉네임 변경에 실패했습니다.\"", Toast.LENGTH_SHORT
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

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_change_nickname
    }
}