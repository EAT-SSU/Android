package com.eatssu.android

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 액션바 커스텀 레이아웃 설정
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_pre) // 뒤로 가기 버튼 아이콘 설정
        val customActionBar = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null)
        actionBar?.customView = customActionBar
    }

    fun setActionBarTitle(title: String) {
        // 액션바 타이틀 설정
        val tvTitle = supportActionBar?.customView?.findViewById<TextView>(R.id.tv_title)
        tvTitle?.text = title
    }
}
