package com.eatssu.android.ui.mypage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
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
            val intent = Intent(this, ChangePwActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.icNicknameChange.setOnClickListener(){
            val intent = Intent(this, ChangeNicknameActivity::class.java)
            startActivity(intent)
            //finish()
        }
        binding.clReview.setOnClickListener(){
            val intent = Intent(this, MyReviewListActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvLogout.setOnClickListener(){

            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃",
                    DialogInterface.OnClickListener { dialog, id ->
                        //탈퇴처리
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }

        binding.tvSignout.setOnClickListener(){

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
}