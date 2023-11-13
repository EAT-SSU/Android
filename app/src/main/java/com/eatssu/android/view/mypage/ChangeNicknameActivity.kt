package com.eatssu.android.view.mypage

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.RetrofitImpl.retrofit
import com.eatssu.android.data.model.request.ChangeNicknameRequestDto
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityChangeNicknameBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeNicknameActivity : BaseActivity<ActivityChangeNicknameBinding>(ActivityChangeNicknameBinding::inflate) {
    private var inputNickname: String = ""
    private var isEnableNickname: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "닉네임 설정" // 툴바 제목 설정

        binding.btnCheckNickname.isEnabled=false
        binding.btnChNicknameDone.isEnabled=false

        binding.etChNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                inputNickname = binding.etChNickname.text.toString()
                //값 유무에 따른 활성화 여부
                if (binding.etChNickname.text != null) {
                    binding.btnCheckNickname.isEnabled =true //있다면 true 없으면 false
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.btnCheckNickname.setOnClickListener{
            val userService =
                retrofit.create(UserService::class.java)
            userService.nicknameCheck(inputNickname).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Log.d("post", "onResponse 성공: " + response.body().toString());

                        if(response.body()=="true"){
                            Toast.makeText(
                                this@ChangeNicknameActivity, "사용가능한 닉네임 입니다.", Toast.LENGTH_SHORT
                            ).show()
                            isEnableNickname=true
                            binding.btnChNicknameDone.isEnabled=isEnableNickname
                        }else{

                            Toast.makeText(
                                this@ChangeNicknameActivity, "이미 사용 중인 닉네임 입니다..", Toast.LENGTH_SHORT
                            ).show()
                            isEnableNickname=false
                            binding.btnChNicknameDone.isEnabled=isEnableNickname
                        }
                    } else {
                        Toast.makeText(
                            this@ChangeNicknameActivity, "닉네임 중복 확인에 실패했습니다.\"", Toast.LENGTH_SHORT
                        ).show()
                        Log.d("post", "onResponse 실패: " + response.body().toString())
                        isEnableNickname=false
                        binding.btnChNicknameDone.isEnabled=isEnableNickname
                    }
                }


                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("post", "onFailure 에러: " + t.message.toString());
                    Toast.makeText(
                        this@ChangeNicknameActivity, "닉네임 중복 확인에 실패했습니다.\"", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }


        binding.btnChNicknameDone.setOnClickListener{

            val userService =
                retrofit.create(UserService::class.java)
            userService.changeNickname(ChangeNicknameRequestDto(inputNickname)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("post", "onResponse 성공: " + response.body().toString());

                        if (response.code() == 200) {
                            Toast.makeText(
                                this@ChangeNicknameActivity, "닉네임 변경에 성공했습니다.", Toast.LENGTH_SHORT
                            ).show()
                            MySharedPreferences.setUserName(this@ChangeNicknameActivity,inputNickname)
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

}