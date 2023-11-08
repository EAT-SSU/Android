package com.eatssu.android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.eatssu.android.R


abstract class BaseActivity<B : ViewBinding>(
    val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> B
) : AppCompatActivity() {

    private var _binding: B? = null
    val binding get() = _binding!!

    protected lateinit var toolbar: Toolbar
    protected lateinit var toolbarTitle: TextView
    protected lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(findViewById(R.id.toolbar))

        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.toolbar_title)
        backBtn =findViewById(R.id.btn_back)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바 기본 제목 비활성화

        backBtn.setOnClickListener {
            finish()
        }

        _binding = bindingFactory(layoutInflater, findViewById(R.id.content_frame), true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
