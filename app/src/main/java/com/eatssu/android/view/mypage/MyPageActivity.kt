package com.eatssu.android.view.mypage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eatssu.android.App
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.model.response.GetMyInfoResponseDto
import com.eatssu.android.data.service.MyPageService
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.view.login.SocialLoginActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageActivity : BaseActivity<ActivityMyPageBinding>(ActivityMyPageBinding::inflate) {
    private lateinit var userService: UserService

    private val firebaseRemoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "마이페이지" // 툴바 제목 설정

        loadMyInfo()

        // Firebase Remote Config 초기화 설정
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 캐시된 값을 1시간마다 업데이트
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        // 기본값 설정 (강제 업데이트 여부와 버전 정보)
        val defaultValues: Map<String, Any> = mapOf(
            "force_update_required" to false,
            "latest_app_version" to "1.0.0"
        )
        firebaseRemoteConfig.setDefaultsAsync(defaultValues)

        // Firebase Remote Config 데이터 가져오기
        fetchRemoteConfig()

        supportActionBar?.title = "마이페이지"

        binding.tvNickname.text = MySharedPreferences.getUserName(this)
        //binding.tvEmail.text = MySharedPreferences.getUserEmail(this)

        binding.clNickname.setOnClickListener{
            val intent = Intent(this, ChangeNicknameActivity::class.java)
            startActivity(intent)
            //finish()
        }

        binding.tvMyreview.setOnClickListener{
            val intent = Intent(this, MyReviewListActivity::class.java)
            startActivity(intent)
//            finish()
        }

        val intent = Intent(this, SocialLoginActivity::class.java)

        binding.tvLogout.setOnClickListener{

            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃",
                    DialogInterface.OnClickListener { dialog, id ->
                        //로그아웃
                        MySharedPreferences.clearUser(this)
                        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        App.token_prefs.clearTokens() //자동로그인 토큰 날리기
                        startActivity(intent)
                    })
                .setNegativeButton("취소") { _, _ ->
                }
            // 다이얼로그를 띄워주기
            builder.show()
        }

        binding.tvSignout.setOnClickListener{

            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(this)
            builder.setTitle("탈퇴하기")
                .setMessage("탈퇴 하시겠습니까?")
                .setPositiveButton("탈퇴하기"
                ) { _, _ ->
                    //탈퇴처리
                    MySharedPreferences.clearUser(this)
                    Toast.makeText(this, "탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                    App.token_prefs.clearTokens() //자동로그인 토큰 날리기
                    userService.signOut() //탈퇴하기 API 호출
                    startActivity(intent)
                }
                .setNegativeButton("취소") { _, _ ->
                }
            // 다이얼로그를 띄워주기
            builder.show()
        }
    }

    private fun fetchRemoteConfig() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Remote Config 데이터 가져오기 성공
                    binding.tvAppVersion.text = firebaseRemoteConfig.getString("app_version")                }
            }
    }

    fun loadMyInfo() {
        val service = RetrofitImpl.retrofit.create(MyPageService::class.java)
        service.getMyInfo()
            .enqueue(object : Callback<GetMyInfoResponseDto> {
                override fun onResponse(
                    call: Call<GetMyInfoResponseDto>,
                    response: Response<GetMyInfoResponseDto>
                ) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            binding.tvNickname.text = response.body()!!.nickname
                            Log.d("post", "onResponse 성공: " + response.body().toString())

                        } else {
                            Log.d("post", "onResponse 오류: " + response.body().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<GetMyInfoResponseDto>, t: Throwable) {
                    Log.d("post", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}