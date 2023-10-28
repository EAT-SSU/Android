package com.eatssu.android.view.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.App
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.RetrofitImpl.nonRetrofit
import com.eatssu.android.data.model.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.model.response.TokenResponseDto
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.databinding.ActivitySocialLoginBinding
import com.eatssu.android.view.main.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URISyntaxException


class SocialLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySocialLoginBinding
    val KAKAOTAG = "카카오소셜로그인"

    var userEmail: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocialLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // SharedPreferences 안에 값이 저장되어 있을 때-> Login 패스하기
        if (MySharedPreferences.getUserEmail(this).isNotBlank()) {
            // SharedPreferences 안에 값이 저장되어 있을 때 -> MainActivity로 이동
            Toast.makeText(
                this,
                "자동 로그인 되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val context = this


        binding.imbKakao.setOnClickListener {

            Log.d("post", "버튼 클릭")
            kakaoSignIn()
//            lifecycleScope.launch {
//                try {
//                    // 서비스 코드에서는 간단하게 로그인 요청하고 oAuthToken 을 받아올 수 있다.
//                    val oAuthToken = UserApiClient.loginWithKakao(context)
//                    Log.d("MainActivity", "beanbean > $oAuthToken")
//                    postUserInfo();
//
//                } catch (error: Throwable) {
//                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
//                        Log.d("MainActivity", "사용자가 명시적으로 취소")
//                    } else {
//                        Log.e("MainActivity", "인증 에러 발생", error)
//                    }
//                }
//            }
//        }
        }
    }

    private fun kakaoSignIn() { // 카카오 소셜로그인
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d(KAKAOTAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.d(KAKAOTAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                kakaoInfo()
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.d(KAKAOTAG, "카카오톡으로 로그인 실패", error)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this@SocialLoginActivity, callback = callback)
                } else if (token != null) {
                    Log.i(KAKAOTAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    kakaoInfo()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this@SocialLoginActivity, callback = callback)
        }
    }

    fun kakaoInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.d(KAKAOTAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                userEmail = user.kakaoAccount?.email
                Log.d("kakao","info")
//                socialSignInApi(userEmail!!,"KAKAO")
                // Toast.makeText(this@SignIn, "email : $userEmail pw : $userPw", Toast.LENGTH_LONG).show()
            }
        }
    }

//    private fun socialSignInApi(userEmail: String, userPw: String, platform: String) {
//        val service = nonRetrofit.create(OauthService::class.java)
//
//        val LoginInfo = LoginRequest(userEmail, userPw)
//        service.oauthLogIn(LoginInfo).enqueue(object : Callback<BaseResponse<TokenResponse>> {
//
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun onResponse(
//                call: Call<BaseResponse<TokenResponse>>,
//                response: Response<BaseResponse<TokenResponse>>,
//            ) {
//                val code = response.body()?.code
//
//                if (response.isSuccessful) {
//                    if (code == 200) {
//                        // val mIntent = Intent(this@SignIn, BottomNavi::class.java)
//                        Log.d("SignInDebug", "소셜 로그인 성공~ : " + response.headers().toString())
//
//                        // 자동로그인
//                        UserSharedPreferences.setUserEmail(this@SignInActivity, userEmail)
//                        UserSharedPreferences.setUserPw(this@SignInActivity, userPw)
//                        //
//
//                        // 토큰저장
//                        App.token_prefs.accessToken = response.body()!!.result?.accessToken
//                        App.token_prefs.refreshToken = response.body()!!.result?.refreshToken
//
//                        Log.d("로그인 sh 확인 로그 이메일", UserSharedPreferences.getUserEmail(this@SignInActivity))
//                        Log.d("로그인 sh 확인 로그 비번", UserSharedPreferences.getUserPw(this@SignInActivity))
//                        Log.d("로그인 sh 확인 로그 서버에서 온 액세스 토큰", App.token_prefs.accessToken.toString())
//                        Log.d("로그인 sh 확인 로그 서버에서 온 리프래시 토큰", App.token_prefs.refreshToken.toString())
//                        Log.d("로그인 sh 확인 로그 액세스 토큰", App.token_prefs.accessToken.toString())
//                        Log.d("로그인 sh 확인 로그 리프래시 토큰", App.token_prefs.refreshToken.toString())
//
//                        moveToHome()
//                        // startActivity(mIntent)
//                    } else {
//                        Log.d(
//                            "SignInDebug",
//                            "아이디 비번 틀림",
//                        )
//                        customSingInPopupWindow(
//                            this@SignInActivity,
//                            ALERT_TEXT_SIGN_IN,
//                            binding.root,
//                            binding.btnSigninSignin,
//                        )
//                    }
//                } else {
//                    Log.d(
//                        "SignInDebug",
//                        "소셜로그인, 정보 존재하지 않음. 회원가입 필요한경우임 ${response?.toString()}",
//                    )
//                    val mIntent = Intent(this@SignInActivity, NicknameActivity::class.java)
//                    mIntent.putExtra("email", userEmail)
//                    mIntent.putExtra("password", userPw)
//                    mIntent.putExtra("platform", platform)
//                    startActivity(mIntent)
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse<TokenResponse>>, t: Throwable) {
//                Log.d("SignInDebug", "onFailure 에러: " + t.message.toString())
//            }
//        })
//    }
    private fun postUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (user != null) {
                // 유저의 아이디
                Log.d(TAG, "invoke: id =" + user.id)
                val providerID = user.id.toString()
                // 유저의 이메일
                Log.d(TAG, "invoke: email =" + user.kakaoAccount!!.email)
                val email = user.kakaoAccount!!.email.toString()


                val intent = Intent(this, MainActivity::class.java)

                val service = nonRetrofit.create(OauthService::class.java)
                service.loginWithKakao(LoginWithKakaoRequestDto(email,providerID))
                    .enqueue(object : Callback<TokenResponseDto> {
                        override fun onResponse(
                            call: Call<TokenResponseDto>,
                            response: Response<TokenResponseDto>
                        ) {
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    Log.d("post", "onResponse 성공: " + response.body().toString());

                                    /*자동 로그인*/
                                    MySharedPreferences.setUserEmail(this@SocialLoginActivity,email)
                                    MySharedPreferences.setUserPlatform(this@SocialLoginActivity,"KAKAO")

                                    /*토큰 저장*/
                                    App.token_prefs.accessToken = response.body()!!.accessToken
                                    App.token_prefs.refreshToken =
                                        response.body()!!.refreshToken//헤더에 붙일 토큰 저장

                                    Toast.makeText(this@SocialLoginActivity, "$email 계정으로 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)  // 화면 전환을 시켜줌
                                    finish()
                                } else {
                                    Log.d("post", "onResponse 오류: " + response.body().toString());
                                    Toast.makeText(this@SocialLoginActivity, "error: " + response.message(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<TokenResponseDto>, t: Throwable) {
                            Log.d("post", "onFailure 에러: " + t.message.toString());
                        }
                    })
            }
        }
    }

}