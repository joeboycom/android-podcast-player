package com.joe.podcastplayer.extension

import android.app.Activity
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import com.joe.podcastplayer.base.BaseActivity
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

val View.baseActivity: BaseActivity<*>?
    get() {
        var context = context
        while (context is ContextWrapper) {
            if (context is BaseActivity<*>) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

val View.activity: Activity?
    get() {
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

fun View.setVisibility(visible: Boolean) {
    if (this.visibility == View.GONE && !visible) return
    if (this.visibility == View.VISIBLE && visible) return
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setInvisibility() {
    if (this.visibility == View.INVISIBLE) return
    this.visibility = View.INVISIBLE
}

enum class StreamType {
    None, throttleFirst, debounce
}

fun View.onClick(duration: Long = 500L, streamType: StreamType = StreamType.throttleFirst, action: () -> Unit) {
    val disposable = clicks()
        .let {
            when (streamType) {
                StreamType.throttleFirst -> it.throttleFirst(duration, TimeUnit.MILLISECONDS)
                StreamType.debounce -> it.debounce(duration, TimeUnit.MILLISECONDS)
                StreamType.None -> { /* do nothing */ it }
            }
        }
        .observeOn(AndroidSchedulers.mainThread())
        .let {
            when {
                // get fragment view lifecycle in the feature, it seems no way to get fragment from view currently
                baseActivity != null -> it.bindUntilEvent(baseActivity!!, Lifecycle.Event.ON_DESTROY)
                else -> it
            }
        }
        .subscribe { action.invoke() }
    baseActivity?.addDisposable(disposable)
}

fun View.onLongClick(action: () -> Unit) {
    val disposable = longClicks()
        .observeOn(AndroidSchedulers.mainThread())
        .let {
            when {
                // get fragment view lifecycle in the feature, it seems no way to get fragment from view currently
                baseActivity != null -> it.bindUntilEvent(baseActivity!!, Lifecycle.Event.ON_DESTROY)
                else -> it
            }
        }
        .subscribe { action.invoke() }
    baseActivity?.addDisposable(disposable)
}

val ViewGroup.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)
