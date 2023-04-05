package com.eatssu.android.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.eatssu.android.MainActivity
import com.eatssu.android.R
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.request.SignUpRequest
import com.eatssu.android.data.model.response.TokenResponse
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivitySignUpBinding
import com.eatssu.android.ui.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding

    //메시지 담을 변수
    var name: String = ""
    var email: String = ""
    var pw: String = ""
    var pw2: String = ""

    val emailPattern =
        "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"
    val pwPattern = "^.*(?=^.{5,15}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#\$%^&+=]).*$"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_sign_up, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

        supportActionBar?.title = "회원가입"

        //닉네임
        binding.etSigninNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                name = binding.etSigninNickname.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        binding.etSigninEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                email = binding.etSigninEmail.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        //(! @ # $ % ^ &amp; + =
        binding.etSigninPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                pw = binding.etSigninPw.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.etSigninPw2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                pw2 = binding.etSigninPw2.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        //중복체크
        binding.btnEmailExist.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)  // 인텐트를 생성해줌,

            val userService =
                RetrofitImpl.getApiClientWithOutToken().create(UserService::class.java)
            userService.getEmailExist(email).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        Log.d("post", "onResponse 성공: " + response.body().toString());

                        if (response.body() == true) {
                            Toast.makeText(
                                this@SignUpActivity, "실패 메세지 띄우기 입니다.", Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                this@SignUpActivity, "성공 메세지 띄우기 입니다.", Toast.LENGTH_SHORT
                            ).show()

                            startActivity(intent)  // 화면 전환을 시켜줌
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.d("post", "onFailure 에러: " + t.message.toString());
                }
            })

        }

        //회원가입하기
        binding.btnSigninDone.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)  // 인텐트를 생성해줌,

            val userService =
                RetrofitImpl.getApiClientWithOutToken().create(UserService::class.java)
            userService.signUp(SignUpRequest(email, name, pw))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onResponse(
                        call: Call<TokenResponse>, response: Response<TokenResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("post", "onResponse 성공: " + response.body().toString());

                            if (response.code() == 200) {
                                Toast.makeText(
                                    this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT
                                ).show()
                                startActivity(intent)  // 화면 전환을 시켜줌
                                finish()
                            } else {
                                Toast.makeText(
                                    this@SignUpActivity, "실패 메세지 띄우기 입니다.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Log.d("post", "onFailure 에러: " + t.message.toString());
                    }
                })

        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_sign_up
    }
}