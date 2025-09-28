package com.student.githubdemo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {
    
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected == true
        }
    }
    
    fun getNetworkErrorMessage(throwable: Throwable): String {
        return when {
            throwable.message?.contains("timeout", ignoreCase = true) == true -> 
                "网络请求超时，请检查网络连接"
            throwable.message?.contains("unable to resolve host", ignoreCase = true) == true -> 
                "无法连接到服务器，请检查网络连接"
            throwable.message?.contains("no internet", ignoreCase = true) == true -> 
                "网络不可用，请检查网络设置"
            else -> "网络错误：${throwable.message}"
        }
    }
}
