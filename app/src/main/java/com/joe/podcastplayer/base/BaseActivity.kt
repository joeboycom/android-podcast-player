package com.joe.podcastplayer.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.joe.podcastplayer.utility.ImageLoader
import com.joe.podcastplayer.viewModel.component.ViewModelFactory
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.TimeUnit

interface ICoreActivity {
    fun initName()
    fun initIntent()
    fun initContentView()
    fun init()
    fun initLayout()
    fun initAction()
    fun initObserver()
}

open class BaseActivity<T : ViewBinding> : AppCompatActivity(), ICoreActivity {

    private var timerDisposable: Disposable? = null
    private var resumeTimes = 0

    private var compositeDisposable = CompositeDisposable()
    lateinit var viewBinding: T
    var screenName: String? = null
    var autoSendScreen = true
    var enableActivityTimer = false
        set(value) {
            field = value
            if (!isFinishing) resumeTimer()
        }
    var screenAliveDuration = 0
    val isFirstIn: Boolean get() = (resumeTimes <= 1)
    var isResume = false
        private set
    var isStopped = false
        private set

    var viewModelFactory = ViewModelFactory()

    private val imageLoader: ImageLoader by lazy { ImageLoader(this) }

    private fun addResumeTime() {
        resumeTimes += 1
    }

    private fun resumeTimer() {
        if (!enableActivityTimer) return
        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
            .doOnNext { screenAliveDuration += 1 }
            .subscribe()
    }

    private fun pauseTimer() {
        if (enableActivityTimer && timerDisposable != null && !timerDisposable!!.isDisposed) timerDisposable!!.dispose()
    }

    fun addDisposable(disposable: Disposable): Disposable {
        compositeDisposable.add(disposable)
        return disposable
    }

    fun sendScreen() {
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) {
            super.attachBaseContext(newBase)
            return
        }
        super.attachBaseContext(newBase)
    }

    /* some view will get coreActivity.screenName inside the activity
    * but some of activity handle screenName in initIntent()
    * so make initIntent() above of initContentView() here
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initName()
        initIntent()
        initContentView()
        clear()
        init()
        initLayout()
        initAction()
        initObserver()
    }

    /***
     * clear all fragment
     * when activity kill by system, fragment still alive,
     * in order to avoid old fragment still appear on activity, clear all fragment is necessary
     ***/
    private fun clear() {
        for (fragment in supportFragmentManager.fragments) supportFragmentManager.beginTransaction().remove(fragment).commit()
    }

    override fun initName() {}

    override fun initContentView() {
        fetchViewBinding()
    }

    private fun fetchViewBinding() {
        val clazz = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
        val method: Method = clazz.getMethod("inflate", LayoutInflater::class.java)
        viewBinding = method.invoke(null, layoutInflater) as T
        setContentView(viewBinding.root)
    }

    override fun initIntent() {}

    override fun init() {}

    override fun initLayout() {}

    override fun initAction() {}

    override fun initObserver() {}

    // endregion

    // region lifecycle

    override fun onResume() {
        super.onResume()
        isResume = true
        if (autoSendScreen) sendScreen()
        addResumeTime()
        resumeTimer()
    }

    override fun onPause() {
        super.onPause()
        pauseTimer()
        isResume = false
    }

    override fun onStop() {
        isStopped = true
        super.onStop()
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
        super.onDestroy()
    }

    // endregion

    // region extension - image loader

    fun ImageView.load(
        url: String?,
        cornerRadius: Int = 0,
        cornerType: RoundedCornersTransformation.CornerType? = null,
        blurRadius: Int = 0,
        enableFadeTransition: Boolean = true,
        @DrawableRes placeHolderResId: Int? = null,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes fallbackResId: Int? = null,
        fallbackDrawable: Drawable? = null
    ) {
        imageLoader.load(url, this, cornerRadius, cornerType, blurRadius, enableFadeTransition, placeHolderResId, placeHolderDrawable, fallbackResId, fallbackDrawable)
    }

    fun ImageView.load(
        @DrawableRes redId: Int,
        cornerRadius: Int = 0,
        cornerType: RoundedCornersTransformation.CornerType? = null,
        blurRadius: Int = 0,
        enableFadeTransition: Boolean = true,
        @DrawableRes placeHolderResId: Int? = null,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes fallbackResId: Int? = null,
        fallbackDrawable: Drawable? = null
    ) {
        imageLoader.load(redId, this, cornerRadius, cornerType, blurRadius, enableFadeTransition, placeHolderResId, placeHolderDrawable, fallbackResId, fallbackDrawable)
    }
    // endregion
}
