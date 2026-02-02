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
import com.eatssu.android.presentation.mypage.language.LanguageSelectorActivity
import com.eatssu.android.presentation.util.showDialog
import com.eatssu.android.presentation.util.showErrorToast
import com.eatssu.android.presentation.util.showInfoToast
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.EventLogger
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ScreenId
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.talk.TalkApiClient
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
                                render(ui.data)
                            }

                            is UiState.Error -> {
                                showErrorToast(R.string.not_found)
                            }
                        }
                    }
                }
                launch {
                    myPageViewModel.uiEvent.collectLatest { event ->
                        when (event) {
                            is UiEvent.ShowToast -> showToast(event)
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
            binding.tvNickname.text = getString(R.string.set_nickname)
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
                showInfoToast(getString(R.string.toast_notification_enable, formattedDate))
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
            showInfoToast(getString(R.string.toast_notification_disable, formattedDate))
        }
    }

    private fun setOnClickListener() {
        binding.llMyInfo.setOnClickListener {
            startActivity(Intent(requireContext(), UserInfoActivity::class.java))
        }

        binding.llInquire.setOnClickListener {
            val context = requireContext()
            val channelPublicId = "_ZlVAn"

            TalkApiClient.instance.chatChannel(context, channelPublicId) {
                val url = TalkApiClient.instance.chatChannelUrl(channelPublicId)
                KakaoCustomTabsClient.openWithDefault(context, url)
            }

            EventLogger.screenView(ScreenId.EXTERNAL_INQUIRE)
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
        requireContext().run {
            showDialog(
                title = getString(R.string.dialog_notification_permission_title),
                description = getString(R.string.dialog_notification_permission_description)
            ) {
                confirmText = getString(R.string.dialog_settings)
                cancelText = getString(R.string.button_cancel)
                onConfirm { dialog ->
                    openAppNotificationSettings(this@run)
                    dialog.dismiss()
                }
            }
        }
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
        requireContext().run {
            showDialog(getString(R.string.dialog_logout_title), getString(R.string.dialog_logout_message)) {
                isDestructive = true
                onConfirm {
                    mainViewModel.logOut() // 로그아웃은 메인 액티비티에서 처리하도록 수정
                    startActivity(Intent(this@run, LoginActivity::class.java))
                }
            }
        }
    }

    private fun moveToOss() {
        try {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        } catch (e: Exception) {
            showErrorToast(getString(R.string.toast_oss_load_fail))
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
