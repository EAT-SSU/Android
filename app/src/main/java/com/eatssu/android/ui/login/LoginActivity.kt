package com.eatssu.android.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.MainActivity
import com.eatssu.android.App
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.request.LoginRequest
import com.eatssu.android.data.model.response.TokenResponse
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var email: String = ""
    private var pw: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 안에 값이 저장되어 있지 않을 때 -> Login
        if (MySharedPreferences.getUserId(this).isBlank()
            || MySharedPreferences.getUserPw(this).isBlank()
        ) {
        } else { // SharedPreferences 안에 값이 저장되어 있을 때 -> MainActivity로 이동
            Toast.makeText(
                this,
                "${MySharedPreferences.getUserId(this)}, 자동 로그인 되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.tvLookAround.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
            finish()
        }

        //비밀번호 찾기
        binding.tvFindPw.setOnClickListener {
//            val intent = Intent(this, LoginFindPwActivity::class.java)
            startActivity(intent)  // 화면 전환을 시켜줌
            finish()
        }

        //회원가입하기
        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)  // 화면 전환을 시켜줌
//            finish()
        }


        //버튼 비활성화
//        binding.btnLogin.isEnabled = false

        //EditText 값 있을때만 버튼 활성화
        binding.etLoginEmail.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                email = binding.etLoginEmail.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        //EditText 값 있을때만 버튼 활성화
        binding.etLoginPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                pw = binding.etLoginPw.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        //SharedPref.openSharedPrep(this)

        //로그인 버튼 -> 메인
        //회원여부 판단하는 코드 작성 필요
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            val service = RetrofitImpl.getApiClientWithOutToken().create(UserService::class.java)
            service.logIn(LoginRequest(email, pw)).enqueue(object : Callback<TokenResponse> {
                override fun onResponse(
                    call: Call<TokenResponse>,
                    response: Response<TokenResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            Log.d("post", "onResponse 성공: " + response.body().toString());
                            MySharedPreferences.setUserId(this@LoginActivity, email)
                            MySharedPreferences.setUserPw(this@LoginActivity, pw)//자동로그인 구현

                            App.token_prefs.accessToken = response.body()!!.accessToken
                            App.token_prefs.refreshToken =
                                response.body()!!.refreshToken//헤더에 붙일 토큰 저장

                            Toast.makeText(
                                this@LoginActivity,
                                email + " 계정으로 로그인에 성공하였습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(intent)  // 화면 전환을 시켜줌
                            finish()
                        } else {
                            Log.d("post", "onResponse 오류: " + response.body().toString());
                            Toast.makeText(
                                this@LoginActivity,
                                "error: " + response.message(),
                                Toast.LENGTH_SHORT
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
}