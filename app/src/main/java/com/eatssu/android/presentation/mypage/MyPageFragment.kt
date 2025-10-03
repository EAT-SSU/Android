package com.eatssu.android.presentation.mypage

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eatssu.android.R
import com.eatssu.android.databinding.FragmentMyPageBinding
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.base.BaseFragment
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.mypage.myreview.MyReviewListComposeActivity
import com.eatssu.android.presentation.mypage.terms.WebViewActivity
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ScreenId
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(ScreenId.MYPAGE_MAIN) {

    private val myPageViewModel: MyPageViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun setBinding(layoutInflater: LayoutInflater): FragmentMyPageBinding {
        return FragmentMyPageBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSignout.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        setupObservers()
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()
        myPageViewModel.fetchMyInfo() // 닉네임 변경 등으로부터 복귀 시 정보 갱신
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    myPageViewModel.uiState.collectLatest { ui ->
                        when (ui) {
                            is UiState.Init, UiState.Loading -> Unit // 닉네임만 불러옴으로 로딩 인디케이터 없음
                            is UiState.Success -> {
                                ui.data?.let { render(it) }
                            }

                            is UiState.Error -> {
                                showToast(getString(R.string.not_found))
                            }
                        }
                    }
                }
                launch {
                    myPageViewModel.uiEvent.collectLatest { event ->
                        when (event) {
                            is UiEvent.ShowToast -> showToast(event.message)
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun render(state: MyPageState) {
        // 앱 버전
        binding.tvAppVersion.text = state.appVersion

        // 닉네임
        if (state.hasNickname) {
            binding.tvNickname.text = state.nickname
        } else {
            // 필요 시 미설정 안내 문구
            binding.tvNickname.text = "닉네임을 설정해주세요"
        }

        // 알람 스위치 (리스너 잠시 해제 후 값 반영)
        binding.alarmSwitch.setOnCheckedChangeListener(null)
        binding.alarmSwitch.isChecked = state.isAlarmOn
        binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleAlarmSwitchChange(isChecked)
        }
    }

    private fun handleAlarmSwitchChange(isChecked: Boolean) {
        val nowDatetime = LocalDateTime.now()
        val formattedDate = nowDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

        if (isChecked) {
            if (checkNotificationPermission(requireContext())) {
                myPageViewModel.setNotificationOn()
                showToast("EAT-SSU 알림 수신을 동의하였습니다.\n$formattedDate")
            } else {
                showNotificationPermissionDialog()
                // 권한 미허용이면 스위치 원복
                binding.alarmSwitch.setOnCheckedChangeListener(null)
                binding.alarmSwitch.isChecked = false
                binding.alarmSwitch.setOnCheckedChangeListener { _, checked ->
                    handleAlarmSwitchChange(checked)
                }
            }
        } else {
            myPageViewModel.setNotificationOff()
            showToast("EAT-SSU 알림 수신을 거부하였습니다.\n$formattedDate")
        }
    }

    private fun setOnClickListener() {
        binding.llMyInfo.setOnClickListener {
            startActivity(Intent(requireContext(), UserInfoActivity::class.java))
        }

        binding.llInquire.setOnClickListener {
            startWebView(
                getString(R.string.kakao_talk_channel_url),
                getString(R.string.contact),
                ScreenId.EXTERNAL_INQUIRE
            )
        }

        binding.llMyReview.setOnClickListener {
            startActivity(Intent(requireContext(), MyReviewListComposeActivity::class.java))
        }

        binding.tvLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.llSignout.setOnClickListener {
            // 현재 Success 상태에서 안전하게 닉네임 추출
            val nickname = (myPageViewModel.uiState.value as? UiState.Success)?.data?.nickname
            Intent(requireContext(), SignOutActivity::class.java).apply {
                putExtra("nickname", nickname)
                startActivity(this)
            }
        }

        binding.llDeveloper.setOnClickListener {
            startActivity(Intent(requireContext(), DeveloperActivity::class.java))
        }

        binding.llOss.setOnClickListener { moveToOss() }

        binding.llAppVersion.setOnClickListener { moveToPlayStore() }

        binding.llServiceRule.setOnClickListener {
            startWebView(
                getString(R.string.terms_url),
                getString(R.string.terms),
                ScreenId.EXTERNAL_TERMS
            )
        }

        binding.llPrivateInformation.setOnClickListener {
            startWebView(
                getString(R.string.policy_url),
                getString(R.string.policy),
                ScreenId.EXTERNAL_POLICY
            )
        }
    }

    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("알림 권한 필요")
            .setMessage("알림을 받으려면 알림 권한을 활성화해야 합니다. 설정 화면으로 이동하시겠습니까?")
            .setPositiveButton("설정으로 이동") { _, _ ->
                openAppNotificationSettings(requireContext())
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("로그아웃")
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("로그아웃") { _, _ ->
                mainViewModel.logOut() // 로그아웃은 메인 액티비티에서 처리하도록 수정
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun moveToOss() {
        try {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        } catch (e: Exception) {
            showToast("오픈소스 라이브러리를 불러올 수 없습니다.")
            Timber.e("Error opening OSS Licenses: ${e.message}")
        }
    }

    private fun moveToPlayStore() {
        val appPackageName = requireContext().packageName
        val uri = "market://details?id=$appPackageName".toUri()
        val fallbackUri = "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
        }
    }

    private fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }

    private fun startWebView(url: String, title: String, screenId: ScreenId) {
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtra("URL", url)
            putExtra("TITLE", title)
            putExtra("SCREEN_ID", screenId.name)
        }
        startActivity(intent)
    }
}
