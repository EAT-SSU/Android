package com.eatssu.android.presentation.mypage

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eatssu.android.R
import com.eatssu.android.databinding.FragmentMyPageBinding
import com.eatssu.android.presentation.base.BaseFragment
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.mypage.myreview.MyReviewListActivity
import com.eatssu.android.presentation.mypage.terms.WebViewActivity
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>() {

    private val myPageViewModel: MyPageViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun setBinding(layoutInflater: LayoutInflater): FragmentMyPageBinding {
        return FragmentMyPageBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSignout.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        setupObservers()
        setOnClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myPageViewModel.uiState.collect {
                    binding.tvAppVersion.text = it.appVersion

                    if (it.nickname.isNotEmpty()) {
                        binding.tvNickname.text = it.nickname
                    }

                    binding.alarmSwitch.setOnCheckedChangeListener(null)
                    binding.alarmSwitch.isChecked = it.isAlarmOn
                    binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                        handleAlarmSwitchChange(isChecked)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleAlarmSwitchChange(isChecked: Boolean) {
        val nowDatetime = LocalDateTime.now()
        val formattedDate = nowDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

        if (isChecked) {
            if (checkNotificationPermission(requireContext())) {
                myPageViewModel.setNotificationOn()
                showSnackbar("EAT-SSU 알림 수신을 동의하였습니다.\n$formattedDate")
            } else {
                showNotificationPermissionDialog()
            }
        } else {
            myPageViewModel.setNotificationOff()
            showSnackbar("EAT-SSU 알림 수신을 거부하였습니다.\n$formattedDate")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOnClickListener() {
        binding.llMyInfo.setOnClickListener {
            startActivity(Intent(requireContext(), UserInfoActivity::class.java))
        }

        binding.llInquire.setOnClickListener {
            startWebView(getString(R.string.kakao_talk_channel_url), getString(R.string.contact))
        }

        binding.llMyReview.setOnClickListener {
            startActivity(Intent(requireContext(), MyReviewListActivity::class.java))
        }

        binding.tvLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.llSignout.setOnClickListener {
            Intent(requireContext(), SignOutActivity::class.java).apply {
                putExtra("nickname", myPageViewModel.uiState.value.nickname)
                startActivity(this)
            }
        }

        binding.llDeveloper.setOnClickListener {
            startActivity(Intent(requireContext(), DeveloperActivity::class.java))
        }

        binding.llOss.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        }

        binding.llAppVersion.setOnClickListener {
            moveToPlayStore()
        }

        binding.llServiceRule.setOnClickListener {
            startWebView(getString(R.string.terms_url), getString(R.string.terms))
        }

        binding.llPrivateInformation.setOnClickListener {
            startWebView(getString(R.string.policy_url), getString(R.string.policy))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun moveToPlayStore() {
        val appPackageName = requireContext().packageName
        val uri = Uri.parse("market://details?id=$appPackageName")
        val fallbackUri = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")

        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun startWebView(url: String, title: String) {
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtra("URL", url)
            putExtra("TITLE", title)
        }
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }
}
