package com.eatssu.android.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.eatssu.android.MainActivity
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityChangeNicknameBinding
import com.eatssu.android.ui.BaseActivity

class ChangeNicknameActivity : BaseActivity() {
    private lateinit var binding: ActivityChangeNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeNicknameBinding.inflate(layoutInflater)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_change_nickname, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

        supportActionBar?.title = "닉네임 변경"

        binding.btnChNicknameDone.setOnClickListener(){
            finish()
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_change_nickname
    }
}