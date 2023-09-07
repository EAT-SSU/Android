package com.eatssu.android.view.mypage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.eatssu.android.R
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.base.BaseActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class MyPageActivity : BaseActivity() {
    private lateinit var binding: ActivityMyPageBinding

    private val firebaseRemoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_my_page, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

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
        binding.tvEmail.text = MySharedPreferences.getUserEmail(this)


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
    }

    private fun fetchRemoteConfig() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Remote Config 데이터 가져오기 성공
                    binding.tvAppVersion.text = firebaseRemoteConfig.getString("app_version")                }
            }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_my_page
    }
}