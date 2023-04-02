package com.eatssu.android.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityChangeNicknameBinding
import com.eatssu.android.databinding.ActivityChangePwBinding
import com.eatssu.android.ui.BaseActivity

class ChangePwActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePwBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePwBinding.inflate(layoutInflater)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_change_pw, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

        supportActionBar?.title = "비밀번호 변경"

        binding.btnChPwDone.setOnClickListener(){
            finish()
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_change_pw
    }
}