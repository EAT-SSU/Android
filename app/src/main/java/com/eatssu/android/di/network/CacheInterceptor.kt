package com.eatssu.android.di.network

import android.content.Context
import android.net.ConnectivityManager
import com.eatssu.android.App
import com.eatssu.android.util.RetrofitImpl
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Response

class CacheInterceptor: Interceptor {
    val size = 10 * 1024 * 1024 // 10MB Cache size

    val mCache = Cache(App.appContext.cacheDir, size.toLong())

    fun hasNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = if (RetrofitImpl.hasNetwork(App.appContext))
            request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
        else
            request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
        return chain.proceed(request)
    }

}