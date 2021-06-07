package com.joe.podcastplayer.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.joe.podcastplayer.ImageLoader
import com.joe.podcastplayer.viewModel.component.ViewModelFactory
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

interface ICoreFragment {
    fun enableEventBus(): Boolean
    fun onViewCreated(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    fun initName()
    fun initIntent()
    fun init()
    fun initLayout()
    fun initAction()
    fun initObserver()
}

abstract class BaseFragment<T : ViewBinding> : Fragment(), ICoreFragment {
    var screenName: String? = null
    var fragmentName: String? = null
    var autoSendScreen = true
    private var resumeTimes = 0
    val isFirstIn: Boolean get() = (resumeTimes <= 1)
    lateinit var viewBinding: T

    val imageLoader: ImageLoader by lazy { ImageLoader(this) }

    var viewModelFactory = ViewModelFactory()

    var isViewCreated = false
        private set

    var isAttach = false
        private set

    val baseActivity: BaseActivity<*>?
        get() {
            return activity as? BaseActivity<*>
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val clazz = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
        val method: Method = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        viewBinding = method.invoke(null, layoutInflater, container, false) as T
        onViewCreated(inflater, container, savedInstanceState)
        isViewCreated = true
        return viewBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttach = true
    }

    private fun addResumeTime() {
        resumeTimes += 1
    }

    fun sendScreen() {
        // set screen event
    }

    // region ICoreFragment

    override fun enableEventBus(): Boolean = false

    override fun onViewCreated(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        initName()
        initIntent()
        init()
        initLayout()
        initAction()
        initObserver()
        initEventBus()
    }

    private fun initEventBus() {
        if (!enableEventBus()) return
    }

    // endregion

    // region lifecycle

    override fun onResume() {
        super.onResume()
        if (autoSendScreen) sendScreen()
        addResumeTime()
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

    fun ImageView.loadAvatar(url: String?, iv: ImageView) = imageLoader.loadAvatar(url, this)
}
