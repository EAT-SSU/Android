package com.eatssu.android.presentation.common

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

// 네트워크 연결 확인을 위해 네트워크 변경 시 알람에 사용하는 클래스 NetworkCallback 을 커스터마이징
class NetworkConnection(private val context: Context) :
    ConnectivityManager.NetworkCallback() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR) // 데이터 사용 관련 감지
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) // 와이파이 사용 관련 감지
        .build()

    // 네트워크 연결 안 되어있을 때 보여줄 다이얼로그
    private val dialog: AlertDialog by lazy {
        AlertDialog.Builder(context)
            .setTitle("네트워크 연결 안 됨")
            .setMessage("와이파이 또는 모바일 데이터를 확인해주세요")
            .create()
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

        if (getConnectivityStatus() == null) {
            // 네트워크 연결 안 되어 있을 때
            dialog.show()
        } else {
            // 네트워크 연결 되어 있을 때
            dialog.dismiss()
        }
    }

    // 네트워크 끊겼을 때 실행되는 메소드
    override fun onLost(network: Network) {
        super.onLost(network)
        dialog.show()
    }
}