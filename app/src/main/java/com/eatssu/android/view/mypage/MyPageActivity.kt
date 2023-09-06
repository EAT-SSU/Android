package com.eatssu.android.view.mypage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eatssu.android.R
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.view.login.SocialLoginActivity


class MyPageActivity : BaseActivity() {
    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_my_page, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

        supportActionBar?.title = "마이페이지"

        binding.tvNickname.text = MySharedPreferences.getUserName(this)
        binding.tvEmail.text = MySharedPreferences.getUserEmail(this)

        binding.clNickname.setOnClickListener {
            val intent = Intent(this, ChangeNicknameActivity::class.java)
            startActivity(intent)
            //finish()
        }

        binding.clChPw.setOnClickListener {
            val intent = Intent(this, ChangePwActivity::class.java)
            startActivity(intent)
//            finish()
        }

        binding.clReview.setOnClickListener {
            val intent = Intent(this, MyReviewListActivity::class.java)
            startActivity(intent)
//            finish()
        }

        binding.tvLogout.setOnClickListener {

            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃",
                    DialogInterface.OnClickListener { dialog, id ->
                        //로그아웃
                        MySharedPreferences.clearUser(this)
                        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SocialLoginActivity::class.java)
                        startActivity(intent)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }

        binding.tvSignout.setOnClickListener {

            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(this)
            builder.setTitle("탈퇴하기")
                .setMessage("탈퇴 하시겠습니까?")
                .setPositiveButton("탈퇴하기",
                    DialogInterface.OnClickListener { dialog, id ->
                        //탈퇴처리
                        MySharedPreferences.clearUser(this)
                        Toast.makeText(this, "탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SocialLoginActivity::class.java)
                        startActivity(intent)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_my_page
    }
}