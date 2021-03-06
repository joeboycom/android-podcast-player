package com.joe.podcastplayer.extension

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.joe.podcastplayer.PodcastPlayerApplication
import java.util.*

val Any.className get() = this.javaClass.simpleName

var gson: Gson = GsonBuilder().setPrettyPrinting().create()

fun Any.toJson(): String? {
    return try {
        gson.toJson(this)
    } catch (e: Exception) {
        return null
    }
}

val apiLevel = Build.VERSION.SDK_INT
val osVersion = Build.VERSION.RELEASE!!

val isQOrHigher = Build.VERSION.SDK_INT >= 29
val isPOrHigher = Build.VERSION.SDK_INT >= 28
val isOOrHigher = Build.VERSION.SDK_INT >= 26
val isNOrHigher = Build.VERSION.SDK_INT >= 24
val isMOrHigher = Build.VERSION.SDK_INT >= 23
val isLOrHigher = Build.VERSION.SDK_INT >= 21

val isNetworkAvailable: Boolean
    @SuppressLint("MissingPermission")
    get() {
        val application = PodcastPlayerApplication.application
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (isMOrHigher) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return if (Build.MANUFACTURER.trim { it <= ' ' }.toLowerCase(Locale.US).contains("asus")) {
                activeNetworkInfo != null && activeNetworkInfo.isAvailable
            } else {
                activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }
    }
