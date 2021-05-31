package com.joe.podcastplayer.extension

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min

// https://stackoverflow.com/questions/7085644/how-to-check-if-apk-is-signed-or-debug-build
// after assemble to aar BuildConfig.Debug will always false, so use the code down below and replace BuildConfig.Debug
val Context.isDebugMode: Boolean
    get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

val Context.isTablet: Boolean
    get() {
        return try {
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        } catch (e: Exception) {
            false
        }
    }

private var realMetrics: DisplayMetrics? = null
private fun Context.getRealMetrics(): DisplayMetrics {
    if (realMetrics != null) return realMetrics!!
    val displayMetrics = DisplayMetrics()
    (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(displayMetrics)
    realMetrics = displayMetrics
    return displayMetrics
}

// in pixel 2 it will be 1878
val Context.screenLargerSide: Int get() = max(resources.displayMetrics.heightPixels, resources.displayMetrics.widthPixels)

// in pixel 2 it will be 1080
val Context.screenSmallerSide: Int get() = min(resources.displayMetrics.heightPixels, resources.displayMetrics.widthPixels)

// in pixel 2 it will be 1920
val Context.realScreenLargerSide: Int get() = max(getRealMetrics().heightPixels, getRealMetrics().widthPixels)

// in pixel 2 it will be 1080
val Context.realScreenSmallerSide: Int get() = min(getRealMetrics().heightPixels, getRealMetrics().widthPixels)

val Context.versionName: String?
    get() {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

val Context.versionCode: Long
    get() {
        return try {
            val info = packageManager.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                info.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
    }

// density is for layout
val Context.density: Float
    get() = resources.displayMetrics.density

// scaledDensity is for font
val Context.scaledDensity: Float
    get() = resources.displayMetrics.scaledDensity

fun Context.dp2px(dp: Int): Int {
    return (dp * density).toInt()
}

fun Context.dp2px(dp: Float): Float {
    return dp * density
}

fun Context.sp2px(sp: Float): Float {
    return sp * scaledDensity
}

fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}