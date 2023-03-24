package com.eatssu.android.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.eatssu.android.BaseActivity
import com.eatssu.android.R


class MyPageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setActionBarTitle("마이페이지")


        setContentView(R.layout.activity_my_page)
    }
}