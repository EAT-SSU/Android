package com.eatssu.android.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.eatssu.android.MainActivity
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivitySignInBinding
import com.eatssu.android.ui.BaseActivity

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_sign_in, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)

        supportActionBar?.title = "회원가입"


        binding.btnSigninDone.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
            finish()
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_sign_in
    }
}