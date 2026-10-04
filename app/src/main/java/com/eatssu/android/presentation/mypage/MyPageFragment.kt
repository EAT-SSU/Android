package com.eatssu.android.presentation.mypage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eatssu.android.R
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.android.presentation.MainState
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.mypage.language.LanguageSelectorActivity
import com.eatssu.android.presentation.mypage.myreview.MyReviewListComposeActivity
import com.eatssu.android.presentation.mypage.terms.TermSelectorActivity
import com.eatssu.android.presentation.mypage.terms.WebViewActivity
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.eatssu.android.presentation.util.showDialog
import com.eatssu.android.presentation.util.showErrorToast
import com.eatssu.android.presentation.util.showInfoToast
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.ScreenViewEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.EatssuTheme
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.talk.TalkApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : Fragment() {

    companion object {
        private const val MENU_NOTIFICATION_SETTINGS = "notification_setting"
        private const val MENU_MY_INFO = "my_info"
        private const val MENU_MY_REVIEW = "my_review"
        private const val MENU_INQUIRY = "inquiry"
        private const val MENU_LANGUAGE_SETTING = "language_setting"
        private const val MENU_CREATOR = "creator"
        private const val MENU_LOGOUT = "logout"
        private const val MENU_WITHDRAW = "withdraw"
        private const val MENU_INSTAGRAM = "insta"
    }

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val myPageViewModel: MyPageViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var lastNotificationPermissionState: Boolean? = null
    private val languageSelectorLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mainViewModel.refreshUserDepartmentFromServer()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by myPageViewModel.uiState.collectAsStateWithLifecycle()
                val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

                val notFoundText = stringResource(R.string.not_found)
                val loadingText = stringResource(R.string.widget_loading)
                val affiliationName = when (val state = mainUiState) {
                    is UiState.Success -> {
                        when (val data = state.data) {
                            is MainState.DepartmentState -> listOfNotNull(
                                data.collegeName?.takeIf { it.isNotBlank() },
                                data.departmentName?.takeIf { it.isNotBlank() },
                            ).joinToString(" ").ifBlank { notFoundText }

                            else -> loadingText
                        }
                    }

                    else -> loadingText
                }

                ProvideAnalyticsTracker(analyticsTracker) {
                    EatssuTheme {
                        when (val state = uiState) {
                            is UiState.Success -> {
                                MyPageScreen(
                                    state = state.data,
                                    affiliationName = affiliationName,
                                    onAlarmToggle = ::handleAlarmSwitchChange,
                                    onMyInfoClick = {
                                        mainViewModel.trackMyPageMenu(MENU_MY_INFO)
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                UserInfoActivity::class.java
                                            )
                                        )
                                    },
                                    onMyReviewClick = {
                                        mainViewModel.trackMyPageMenu(MENU_MY_REVIEW)
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                MyReviewListComposeActivity::class.java
                                            )
                                        )
                                    },
                                    onInquireClick = ::openInquire,
                                    onInstagramClick = {
                                        mainViewModel.trackMyPageMenu(MENU_INSTAGRAM)
                                        startWebView(
                                            getString(R.string.eatssu_instagram_url),
                                            getString(R.string.eatssu_instagram),
                                            ScreenId.EXTERNAL_TERMS
                                        )
                                    },
                                    onLanguageSettingClick = {
                                        mainViewModel.trackMyPageMenu(MENU_LANGUAGE_SETTING)
                                        languageSelectorLauncher.launch(
                                            Intent(
                                                requireContext(),
                                                LanguageSelectorActivity::class.java
                                            )
                                        )
                                    },
                                    onTermRulesClick = {
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                TermSelectorActivity::class.java
                                            )
                                        )
                                    },
                                    onDeveloperClick = {
                                        mainViewModel.trackMyPageMenu(MENU_CREATOR)
                                        startWebView(
                                            getString(R.string.developer_url),
                                            getString(R.string.developer),
                                            ScreenId.EXTERNAL_TERMS
                                        )
                                    },
                                    onOssClick = ::moveToOss,
                                    onLogoutClick = ::showLogoutDialog,
                                    onAppVersionClick = ::moveToPlayStore,
                                    onSignOutClick = ::openSignOut,
                                )
                            }

                            UiState.Init, UiState.Loading, UiState.Error -> {
                                Box(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lastNotificationPermissionState = checkNotificationPermission(requireContext())
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        analyticsTracker.track(ScreenViewEvent(ScreenId.MYPAGE_MAIN))
        Timber.d("screen view logging: ${ScreenId.MYPAGE_MAIN}")
        myPageViewModel.fetchMyInfo()
        checkNotificationPermissionChange()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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

    private fun handleAlarmSwitchChange(isChecked: Boolean) {
        mainViewModel.trackMyPageMenu(MENU_NOTIFICATION_SETTINGS)
        if (isChecked) {
            if (checkNotificationPermission(requireContext())) {
                myPageViewModel.setNotificationOn()
            } else {
                showNotificationPermissionDialog()
            }
        } else {
            myPageViewModel.setNotificationOff()
        }
    }

    private fun openInquire() {
        mainViewModel.trackMyPageMenu(MENU_INQUIRY)
        val context = requireContext()
        val channelPublicId = "_ZlVAn"

        TalkApiClient.instance.chatChannel(context, channelPublicId) {
            val url = TalkApiClient.instance.chatChannelUrl(channelPublicId)
            KakaoCustomTabsClient.openWithDefault(context, url)
        }
        analyticsTracker.track(ScreenViewEvent(ScreenId.EXTERNAL_INQUIRE))
    }

    private fun openSignOut() {
        mainViewModel.trackMyPageMenu(MENU_WITHDRAW)
        val nickname = (myPageViewModel.uiState.value as? UiState.Success)?.data?.nickname
        Intent(requireContext(), SignOutActivity::class.java).apply {
            putExtra("nickname", nickname)
            startActivity(this)
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

    private fun checkNotificationPermissionChange() {
        val currentPermissionState = checkNotificationPermission(requireContext())
        
        // 이전 권한 상태가 저장되어 있고, 현재 상태와 다른 경우에만 토스트 표시
        if (lastNotificationPermissionState != null && lastNotificationPermissionState != currentPermissionState) {
            val nowDatetime = LocalDateTime.now()
            val formattedDate = nowDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            
            if (currentPermissionState) {
                showInfoToast(getString(R.string.toast_notification_enable, formattedDate))
                myPageViewModel.setNotificationOn()
            } else {
                showInfoToast(getString(R.string.toast_notification_disable, formattedDate))
                myPageViewModel.setNotificationOff()
            }
        }
        
        lastNotificationPermissionState = currentPermissionState
    }

    private fun showLogoutDialog() {
        mainViewModel.trackMyPageMenu(MENU_LOGOUT)
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
        val context = requireContext()
        try {
            val licensesId = context.resources.getIdentifier("third_party_licenses", "raw", context.packageName)
            val metadataId = context.resources.getIdentifier(
                "third_party_license_metadata",
                "raw",
                context.packageName
            )
            if (licensesId == 0 || metadataId == 0) {
                showErrorToast(getString(R.string.toast_oss_load_fail))
                Timber.e(
                    "OSS raw resource missing. third_party_licenses=$licensesId third_party_license_metadata=$metadataId"
                )
                return
            }

            startActivity(
                Intent(context, OssLicensesMenuActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            )
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
            putExtra(WebViewActivity.EXTRA_URL, url)
            putExtra(WebViewActivity.EXTRA_TITLE, title)
            putExtra("SCREEN_ID", screenId.name)
        }
        startActivity(intent)
    }
}
