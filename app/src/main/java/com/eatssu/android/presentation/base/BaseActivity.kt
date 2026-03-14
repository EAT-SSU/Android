package com.eatssu.android.presentation.base

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.eatssu.android.R
import com.eatssu.android.presentation.common.NetworkConnection
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.util.observeNetworkError
import com.eatssu.android.presentation.util.showInfoToast
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.ScreenViewEvent
import com.eatssu.common.enums.ScreenId
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


abstract class BaseActivity<B : ViewBinding>(
    val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> B,
    val screenId: ScreenId
) : AppCompatActivity() {

    @Inject
    protected lateinit var analyticsTracker: AnalyticsTracker

    private var _binding: B? = null
    val binding get() = _binding!!

    protected lateinit var toolbar: Toolbar
    protected lateinit var toolbarTitle: TextView
    private lateinit var backBtn: MaterialCardView


    private val networkCheck: NetworkConnection by lazy {
        NetworkConnection(this, lifecycleScope)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base)

        setSupportActionBar(findViewById(R.id.toolbar))

        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.toolbar_title)
        backBtn = findViewById(R.id.mcv_setting)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바 기본 제목 비활성화

        backBtn.setOnClickListener {
            finish()
        }

        networkCheck.register() // 네트워크 객체 등록

        _binding = bindingFactory(layoutInflater, findViewById(R.id.fl_content), true)

        // refreshtoken 관리
        observeTokenExpiration()
        observeNetworkError()

        setContainerInset()

    }

    private fun setContainerInset() {
        // Toolbar: topInset만 적용
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.setPadding(
                /* left = */ 0,
                /* top = */ topInset,
                /* right = */ 0,
                /* bottom = */ 0
            )
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fl_content)) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                /* left = */ systemInsets.left,
                /* top = */ 0,
                /* right = */ systemInsets.right,
                /* bottom = */ systemInsets.bottom
            )

            // 소비된 인셋을 반환하여 자식 뷰가 다시 받지 않도록 함
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun observeTokenExpiration() {
        lifecycleScope.launch {
            TokenEventBus.tokenExpired.collect { reason ->
                Timber.i("Logged out due to: $reason")
                showInfoToast(R.string.toast_token_expired)
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finishAffinity()
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


    override fun onResume() {
        super.onResume()

        if (shouldLogScreenId()) {
            analyticsTracker.track(ScreenViewEvent(screenId))
            Timber.d("screen view logging: $screenId")
        }
    }

    open fun shouldLogScreenId(): Boolean = true
}
