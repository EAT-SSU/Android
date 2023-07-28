package com.eatssu.android.ui.login

import RetrofitImpl
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.App
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.model.request.loginWithKakaoRequest
import com.eatssu.android.data.model.response.TokenResponse
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.databinding.ActivitySocialLoginBinding
import com.eatssu.android.ui.main.MainActivity
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

    // 코틀린
    fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url != null && url.startsWith("intent://")) {
            try {
                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                val existPackage =
                    packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
                if (existPackage != null) {
                    startActivity(intent)
                } else {
                    val marketIntent = Intent(Intent.ACTION_VIEW)
                    marketIntent.data =
                        Uri.parse("market://details?id=" + intent.getPackage()!!)
                    startActivity(marketIntent)
                }
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (url != null && url.startsWith("market://")) {
            try {
                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                if (intent != null) {
                    startActivity(intent)
                }
                return true
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }

        }
        view.loadUrl(url)
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocialLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //카카오 로그인에서 제공하는 자동로그인 메소드
//        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//            if (error != null) {
//                Toast.makeText(this, "자동 로그인 실패", Toast.LENGTH_SHORT).show()
//            }
//            else if (tokenInfo != null) {
//                Toast.makeText(this, "자동 로그인 성공", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//            }
//        }

        // SharedPreferences 안에 값이 저장되어 있을 때-> Login 패스하기
        if (App.token_prefs.accessToken?.isNotBlank() == true){ // SharedPreferences 안에 값이 저장되어 있을 때 -> MainActivity로 이동
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
            lifecycleScope.launch {
                try {
                    // 서비스 코드에서는 간단하게 로그인 요청하고 oAuthToken 을 받아올 수 있다.
                    val oAuthToken = UserApiClient.loginWithKakao(context)
                    Log.d("MainActivity", "beanbean > $oAuthToken")
                    postUserInfo();

                } catch (error: Throwable) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.d("MainActivity", "사용자가 명시적으로 취소")
                    } else {
                        Log.e("MainActivity", "인증 에러 발생", error)
                    }
                }
            }
        }

    }

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

                val service = RetrofitImpl.nonRetrofit.create(OauthService::class.java)
                service.loginWithKakao(loginWithKakaoRequest(email, providerID))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onResponse(
                            call: Call<TokenResponse>,
                            response: Response<TokenResponse>
                        ) {
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    Log.d("post", "onResponse 성공: " + response.body().toString());
//                                MySharedPreferences.setUserId(this@LoginActivity, email)
//                                MySharedPreferences.setUserPw(this@LoginActivity, providerID)//자동로그인 구현

                                    App.token_prefs.accessToken = response.body()!!.accessToken
                                    App.token_prefs.refreshToken =
                                        response.body()!!.refreshToken//헤더에 붙일 토큰 저장

                                    Toast.makeText(
                                        this@SocialLoginActivity,
                                        email + " 계정으로 로그인에 성공하였습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(intent)  // 화면 전환을 시켜줌
                                    finish()
                                } else {
                                    Log.d("post", "onResponse 오류: " + response.body().toString());
                                    Toast.makeText(
                                        this@SocialLoginActivity,
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

}