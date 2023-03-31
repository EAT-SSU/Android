package com.eatssu.android.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.BaseActivity
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.databinding.ActivityReviewListBinding


class MyPageActivity : BaseActivity() {
    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        // 커스텀 액션바 설정
        //setCustomActionBar("마이페이지", true)

        setContentView(binding.root)


        binding.clChPw.setOnClickListener(){
            val intent = Intent(this, ChangePwActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
            finish()
        }
        binding.icNicknameChange.setOnClickListener(){
            val intent = Intent(this, ChangeNicknameActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
            //finish()
        }
        binding.clReview.setOnClickListener(){
            val intent = Intent(this, MyReviewListActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
            finish()
        }
    }
}