package com.eatssu.android

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    /*
/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            // 커스텀 액션바를 사용하기 위해 기본 액션바를 숨깁니다.
            hide()
        }
    }

    // 액티비티마다 커스텀 액션바를 설정하기 위한 메서드입니다.
    fun setCustomActionBar(title: String, isHomeEnabled: Boolean) {
        val actionBar = supportActionBar
        actionBar?.apply {
            // 기본 액션바를 숨김 처리한 후, 커스텀 액션바를 추가합니다.
            show()
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)



            // 커스텀 액션바의 타이틀과 홈 버튼을 설정합니다.
            findViewById<TextView>(R.id.tv_title).text = title
            setHomeButtonEnabled(isHomeEnabled)
        }
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        initToolbar()
    }

    abstract fun getLayoutResourceId(): Int

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                // 검색 버튼 클릭 처리
                return true
            }
            R.id.action_settings -> {
                // 설정 버튼 클릭 처리
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

/*
    fun setActionBarTitle(title: String) {
        // 액션바 타이틀 설정
        val tvTitle = supportActionBar?.customView?.findViewById<TextView>(R.id.tv_title)
        tvTitle?.text = title
    }*/

    */
}
