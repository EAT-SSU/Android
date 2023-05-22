package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.eatssu.android.App
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.databinding.ActivityWriteReview1Binding
import com.eatssu.android.ui.BaseActivity

class WriteReview1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReview1Binding.inflate(layoutInflater)

//        val inflater = LayoutInflater.from(this)
//        inflater.inflate(R.layout.activity_write_review1, findViewById(R.id.frame_layout), true)
        setContentView(binding.root)


        binding.included.actionBar.text = "리뷰남기기"

        var MENU_ID: Int = intent.getIntExtra("menuId", -1)
        var menu: String? = intent.getStringExtra("menu")

        var rate: Int = binding.rbReview1.rating.toInt()

        binding.menu.text = menu.toString()
        binding.btnNextReview1.setOnClickListener() {
            //0점 처리
            val intent = Intent(this, WriteReview2Activity::class.java)  // 인텐트를 생성해줌,
            Log.d("intent", "click");
            intent.putExtra("rating", rate)
            intent.putExtra("menuId", MENU_ID)
            intent.putExtra("menu", menu)
            startActivity(intent)  // 화면 전환을 시켜줌
        }
    }
//
//    override fun getLayoutResourceId(): Int {
//        return R.layout.activity_write_review1
//    }
}