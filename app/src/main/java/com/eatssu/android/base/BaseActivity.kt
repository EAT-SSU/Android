package com.eatssu.android.base

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.eatssu.android.R
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.ui.common.AndroidMessageDialogActivity
import com.eatssu.android.ui.common.ForceUpdateDialogActivity
import com.eatssu.android.ui.common.VersionViewModel
import com.eatssu.android.ui.common.VersionViewModelFactory
import com.eatssu.android.util.NetworkConnection
import com.google.android.material.card.MaterialCardView


abstract class BaseActivity<B : ViewBinding>(
    val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> B
) : AppCompatActivity() {

    private var _binding: B? = null
    val binding get() = _binding!!

    protected lateinit var toolbar: Toolbar
    protected lateinit var toolbarTitle: TextView
    protected lateinit var backBtn: MaterialCardView

    private lateinit var versionViewModel: VersionViewModel
    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository

    private val networkCheck: NetworkConnection by lazy {
        NetworkConnection(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        setSupportActionBar(findViewById(R.id.toolbar))

        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.toolbar_title)
        backBtn =findViewById(R.id.mcv_setting)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바 기본 제목 비활성화

        backBtn.setOnClickListener {
            finish()
        }

        networkCheck.register() // 네트워크 객체 등록


        firebaseRemoteConfigRepository = FirebaseRemoteConfigRepository()
        versionViewModel = ViewModelProvider(this, VersionViewModelFactory(firebaseRemoteConfigRepository))[VersionViewModel::class.java]

        if(versionViewModel.checkForceUpdate()){
            showForceUpdateDialog()
        }

//        if(versionViewModel.checkAndroidMessage().dialog) {
//            showAndroidMessageDialog(versionViewModel.checkAndroidMessage().message)
//        }

        _binding = bindingFactory(layoutInflater, findViewById(R.id.fl_content), true)
    }

    override fun onDestroy() {
        super.onDestroy()

        networkCheck.unregister() // 네트워크 객체 해제

        _binding = null
    }


    // 키보드 위 빈 공간을 터치하면 키보드가 사라지도록 한다
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView: View? = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun showForceUpdateDialog() {
        val intent = Intent(this, ForceUpdateDialogActivity::class.java)
        startActivity(intent)
    }

    private fun showAndroidMessageDialog(message: String) {
        val intent = Intent(this, AndroidMessageDialogActivity::class.java)
        intent.putExtra("message",message)
        Log.d("BaseActivity", "공지사항: $message")
        startActivity(intent)
    }
}
