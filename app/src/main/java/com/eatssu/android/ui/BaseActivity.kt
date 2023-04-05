package com.eatssu.android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityBaseBinding


abstract class BaseActivity : AppCompatActivity() {
    private lateinit var customActionBar: Toolbar
    private lateinit var binding: ActivityBaseBinding // BaseActivity 레이아웃 파일에 대한 바인딩 클래스


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        setContentView(R.layout.activity_base)

        customActionBar = findViewById(R.id.toolbar)
        setSupportActionBar(customActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val frameLayout = findViewById<FrameLayout>(R.id.frame_layout)
        val inflater = LayoutInflater.from(this)
        inflater.inflate(getLayoutResourceId(), frameLayout, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    abstract fun getLayoutResourceId(): Int


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}