package com.joe.podcastplayer.extension

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import com.readystatesoftware.systembartint.SystemBarTintManager

fun Activity.useStatusBarAndNavigationBar(@ColorInt statusBarColor: Int, isLightStatusBar: Boolean, navigationBarColor: Int, isLightNavigationBar: Boolean) {
    when {
        isOOrHigher -> {
            window.decorView.systemUiVisibility = 0
            if (isLightStatusBar) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (isLightNavigationBar) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            window.statusBarColor = statusBarColor
            window.navigationBarColor = navigationBarColor
        }
        isMOrHigher -> {
            window.decorView.systemUiVisibility = 0
            if (isLightStatusBar) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.attributes.flags = window.attributes.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            window.statusBarColor = statusBarColor
            val systemBarTintManager = SystemBarTintManager(this)
            systemBarTintManager.setNavigationBarTintEnabled(true)
            systemBarTintManager.setNavigationBarTintColor(navigationBarColor)
        }
        isLOrHigher -> {
            window.attributes.flags = window.attributes.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            window.attributes.flags = window.attributes.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            val systemBarTintManager = SystemBarTintManager(this)
            systemBarTintManager.isStatusBarTintEnabled = true
            systemBarTintManager.setStatusBarTintColor(statusBarColor)
            systemBarTintManager.setNavigationBarTintEnabled(true)
            systemBarTintManager.setNavigationBarTintColor(navigationBarColor)
        }
    }
}