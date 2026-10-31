package com.eatssu.android.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.MenuItem
import android.view.View.GONE
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.WorkManager
import com.eatssu.android.R
import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.event.AnyoneButMeEventPopupController
import com.eatssu.android.presentation.event.AnyoneButMeEventTooltipController
import com.eatssu.android.presentation.favorite.FavoriteDetailFragment
import com.eatssu.android.presentation.favorite.FavoriteFragment
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.mypage.MyPageViewModel
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.eatssu.android.presentation.notification.NotificationActivity
import com.eatssu.android.presentation.util.openAppNotificationSettings
import com.eatssu.android.presentation.util.showDialog
import com.eatssu.android.presentation.util.showInfoToast
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.Time
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(
    ActivityMainBinding::inflate,
    ScreenId.HOME_MAIN
) {
    companion object {
        const val EXTRA_OPEN_MENU_FAVORITES = "open_menu_favorites"
        private const val ENABLE_ANYONE_BUT_ME = false
        private const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1000
    }

    @Inject
    lateinit var notificationStore: NotificationStore

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var anyoneButMeEventPopupController: AnyoneButMeEventPopupController

    @Inject
    lateinit var anyoneButMeEventTooltipController: AnyoneButMeEventTooltipController

    private val mainViewModel: MainViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()
    private lateinit var navController: NavController

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNoToolbar()
        setNavigation()
        if (savedInstanceState == null) {
            openNotificationDestination(intent)
            openMenuFavoritesDestination(intent)
        }
        if (ENABLE_ANYONE_BUT_ME) {
            bindEventPopup(showOnLaunch = savedInstanceState == null)
            bindEventTooltip()
        }

        if (savedInstanceState == null) {
            checkNotificationPermissionDialog()
        }
        collectState()
        collectUiEvents()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openNotificationDestination(intent)
        openMenuFavoritesDestination(intent)
    }

    private fun openMenuFavoritesDestination(intent: Intent) {
        if (!intent.getBooleanExtra(EXTRA_OPEN_MENU_FAVORITES, false)) return
        intent.removeExtra(EXTRA_OPEN_MENU_FAVORITES)
        val args = bundleOf(
            FavoriteFragment.ARG_TODAY_RESTAURANT to intent.getStringExtra(FavoriteFragment.ARG_TODAY_RESTAURANT),
            FavoriteFragment.ARG_TODAY_TIME to intent.getStringExtra(FavoriteFragment.ARG_TODAY_TIME),
            FavoriteFragment.ARG_TODAY_EXPANDED to intent.getBooleanExtra(
                FavoriteFragment.ARG_TODAY_EXPANDED,
                false
            ),
        )
        // A new destination resets the pager to menu favorites, including when partnerships were open.
        navController.navigate(R.id.favoriteFragment, args)
    }

    private fun openNotificationDestination(intent: Intent) {
        val date = intent.getStringExtra(NotificationActivity.EXTRA_MEAL_DATE)
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return
        val time = intent.getStringExtra(NotificationActivity.EXTRA_MEAL_TIME)
            ?.let { value -> Time.entries.firstOrNull { it.name == value } } ?: return
        lifecycleScope.launch {
            intent.getStringExtra("notification_id")?.let { notificationStore.markRead(it) }
        }
        navController.navigate(
            R.id.cafeteria_menu, bundleOf(
                NotificationActivity.EXTRA_MEAL_DATE to date.toString(),
                NotificationActivity.EXTRA_MEAL_TIME to time.name,
            )
        )
        intent.removeExtra(NotificationActivity.EXTRA_MEAL_DATE)
        intent.removeExtra(NotificationActivity.EXTRA_MEAL_TIME)
    }

    private fun setNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNaviBar.itemIconTintList = null

        binding.bottomNaviBar.setOnSingleItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cafeteria_menu -> {
                    navController.navigate(R.id.cafeteria_menu)
                    true
                }

                R.id.map_menu -> {
                    navController.navigate(R.id.mapFragment)
                    true
                }

//                R.id.anyone_but_me_menu -> {
//                    mainViewModel.analyticsPlzNotMe()
//                    anyoneButMeEventPopupController.openAnyoneButMePage()
//                    false
//                }

                R.id.favorite_menu -> {
                    navController.navigate(R.id.favoriteFragment)
                    true
                }

                R.id.mypage_menu -> {
                    navController.navigate(R.id.myPageFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menuItemId = when (destination.id) {
                R.id.cafeteria_menu -> R.id.cafeteria_menu
                R.id.mapFragment -> R.id.map_menu
                R.id.favoriteDetailFragment, R.id.favoriteFragment -> R.id.favorite_menu
                R.id.myPageFragment -> R.id.mypage_menu
                else -> null
            }
            menuItemId?.let { binding.bottomNaviBar.menu.findItem(it).isChecked = true }
        }
    }

    fun openFavoriteTab() {
        if (navController.currentDestination?.id != R.id.favoriteFragment) {
            navController.navigate(R.id.favoriteFragment)
        }
    }

    fun openMapTab() {
        if (navController.currentDestination?.id != R.id.mapFragment) {
            navController.navigate(R.id.mapFragment)
        }
    }

    fun openFavoriteDetail(partnership: PartnershipRestaurant) {
        navController.navigate(
            R.id.favoriteDetailFragment,
            bundleOf(
                FavoriteDetailFragment.ARG_PARTNERSHIP_ID to partnership.id,
                FavoriteDetailFragment.ARG_PARTNERSHIP to partnership,
            ),
        )
    }

    private fun bindEventPopup(showOnLaunch: Boolean) {
        anyoneButMeEventPopupController.bind(
            composeView = binding.composeEventPopup,
            lifecycleScope = lifecycleScope,
            showOnLaunch = showOnLaunch
        )
    }

    private fun bindEventTooltip() {
        anyoneButMeEventTooltipController.bind(
            tooltipComposeView = binding.composeEventTooltip,
            bottomNavigationView = binding.bottomNaviBar
        )
    }

    // set UI --
    private fun setupNoToolbar() {
        // 툴바 사용하지 않도록 설정
        toolbar.let {
            toolbar.visibility = GONE
            toolbarTitle.visibility = GONE
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    // Permission --
    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            val nowDatetime = LocalDateTime.now()
            val formattedDate = nowDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인됨
                showInfoToast(getString(R.string.toast_notification_enable, formattedDate))
                myPageViewModel.setNotificationOn()
            } else {
                // 권한이 거부됨
                // 시스템 권한 다이얼로그가 뜨지 않고 바로 거부된 경우(영구 거부 등) 시스템 설정 화면으로 이동 안내
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    openAppNotificationSettings()
                } else {
                    showInfoToast(getString(R.string.toast_notification_disable, formattedDate))
                    myPageViewModel.setNotificationOff()
                }
            }
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    private fun checkNotificationPermissionDialog() {
        if (isNotificationPermissionGranted()) return

        lifecycleScope.launch {
            if (mainViewModel.isNotificationPermissionDialogDismissed()) return@launch

            showNotificationPrePermissionDialog()
        }
    }

    private fun showNotificationPrePermissionDialog() {
        showDialog(
            title = getString(R.string.dialog_notification_pre_permission_title),
            description = getString(R.string.dialog_notification_pre_permission_description),
        ) {
            cancelText = getString(R.string.button_later)
            confirmText = getString(R.string.button_allow_notification)
            cancellable = true
            onCancel { dialog ->
                mainViewModel.setNotificationPermissionDialogDismissed(true)
                dialog.dismiss()
            }
            onDismiss {
                mainViewModel.setNotificationPermissionDialogDismissed(true)
            }
            onConfirm { dialog ->
                mainViewModel.setNotificationPermissionDialogDismissed(true)
                dialog.dismiss()
                handleAllowNotification()
            }
        }
    }

    private fun handleAllowNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_NOTIFICATION_PERMISSION
            )
        } else {
            if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                val nowDatetime = LocalDateTime.now()
                val formattedDate =
                    nowDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                showInfoToast(getString(R.string.toast_notification_enable, formattedDate))
                myPageViewModel.setNotificationOn()
            } else {
                openAppNotificationSettings()
            }
        }
    }

    // CollectState --
    private fun collectState() {
        lifecycleScope.launch {
            mainViewModel.uiState.collectLatest { state ->
                if (state is UiState.Success) {
                    when (state.data) {
                        is MainState.NicknameNull -> {
                            intent.putExtra("force", true)
                            startActivity<UserInfoActivity>()
                        }

                        is MainState.LoggedOut -> {
                            startActivity<LoginActivity>()
                            finishAffinity()
                        }

                        else -> Unit
                    }
                } else Unit
            }
        }
    }

    // UiEvent 처리
    private fun collectUiEvents() {
        lifecycleScope.launch {
            mainViewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is UiEvent.ShowToast -> showToast(event)
                }
            }
        }
    }

    // 아래 함수를 View 에서는 사용이 어려워 util 로 빼지 않음
    private fun BottomNavigationView.setOnSingleItemSelectedListener(
        minInterval: Long = 500L,
        onSingleItemSelected: (item: MenuItem) -> Boolean
    ) {
        var lastClickTime = 0L

        setOnItemSelectedListener { item ->
            val currentClickTime = SystemClock.uptimeMillis()
            if (currentClickTime - lastClickTime > minInterval) {
                lastClickTime = currentClickTime
                onSingleItemSelected(item)
            } else {
                false // 너무 빠른 클릭 무시
            }
        }
    }

    override fun shouldLogScreenId() = false
}
