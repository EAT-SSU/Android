package com.eatssu.android.presentation.common

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.provider.Settings
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.launch
import com.eatssu.android.presentation.util.showDialog

// 네트워크 연결 확인을 위해 네트워크 변경 시 알람에 사용하는 클래스 NetworkCallback 을 커스터마이징
class NetworkConnection(
    private val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope
) : ConnectivityManager.NetworkCallback() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR) // 데이터 사용 관련 감지
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) // 와이파이 사용 관련 감지
        .build()

    // 네트워크 연결 안 되어있을 때 보여줄 다이얼로그
    private val dialog: Dialog by lazy {
        context.showDialog("네트워크 연결 안 됨", "Wi-Fi, 모바일 데이터를 확인해주세요") {
            cancellable = false
            showCancelButton = false
            showWhenStart = false

            onConfirm {
                context.startActivity(
                    Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                it.dismiss()
            }
        }
    }

    // NetworkCallback 등록
    fun register() {
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    // NetworkCallback 해제
    fun unregister() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    // 현재 네트워크 상태 확인
    fun getConnectivityStatus(): Network? {
        // 연결된 네트워크가 없을 시 null 리턴
        return connectivityManager.activeNetwork
    }

    // 콜백이 등록되거나 네트워크가 연결되었을 때 실행되는 메소드
    override fun onAvailable(network: Network) {
        super.onAvailable(network)

        lifecycleScope.launch {
            if (getConnectivityStatus() == null) {
                // 네트워크 연결 안 되어 있을 때
                dialog.show()
            } else {
                // 네트워크 연결 되어 있을 때
                dialog.dismiss()
            }
        }
    }

    // 네트워크 끊겼을 때 실행되는 메소드
    override fun onLost(network: Network) {
        super.onLost(network)

        // Wi-Fi와 모바일 데이터가 모두 연결된 상태에서 Wi-Fi만 끊겨도 onLost가 호출될 수 있으므로,
        // 현재 활성화 네트워크 여부 검증 필요
        if (getConnectivityStatus() == null) {
            lifecycleScope.launch {
                dialog.show()
            }
        }
    }
}