package com.joe.podcastplayer

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

open class ImageLoader {

    companion object {

        private val invalidAvatars = arrayListOf<String>(
            // "https://cdn.voicetube.com/assets/img/default-avatar",
            // "https://cdn.voicetube.com/assets/img/pp_image2.jpg",
            // "https://cdn-test.voicetube.com/assets/img/default-avatar2.jpg"
        )

        private var avatarOption = RequestOptions().circleCrop()
        private val circleOption = RequestOptions().circleCrop()

        fun addInvalidAvatarUrl(url: String) {
            val exist = invalidAvatars.any { it == url }
            if (!exist) invalidAvatars.add(url)
        }

        fun setAvatarFallbackResId(@DrawableRes resourceId: Int) {
            avatarOption = avatarOption.fallback(resourceId)
        }

        fun setAvatarPlaceHolderResId(@DrawableRes resourceId: Int) {
            avatarOption = avatarOption.placeholder(resourceId)
        }

        fun setCircleFallbackResId(@DrawableRes resourceId: Int) {
            avatarOption = avatarOption.fallback(resourceId)
        }

        fun setCirclePlaceHolderResId(@DrawableRes resourceId: Int) {
            avatarOption = avatarOption.placeholder(resourceId)
        }
    }

    private val context: Context
    private val requestManager: RequestManager

    private val density: Float
        get() {
            return context.resources.displayMetrics.density
        }

    private val Int.dp: Float
        get() {
            return (this.toFloat() * density)
        }

    constructor(activity: Activity) : this(Glide.with(activity), activity)

    constructor(fragment: Fragment) : this(Glide.with(fragment), fragment.activity!!)

    constructor(context: Context) : this(Glide.with(context), context)

    private constructor(requestManager: RequestManager, context: Context) {
        this.context = context
        this.requestManager = requestManager
    }

    fun load(
        @DrawableRes resourceId: Int,
        iv: ImageView,
        cornerRadius: Int = 0,
        cornerType: RoundedCornersTransformation.CornerType? = null,
        blurRadius: Int = 0,
        enableFadeTransition: Boolean = true,
        @DrawableRes placeHolderResId: Int? = null,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes fallbackResId: Int? = null,
        fallbackDrawable: Drawable? = null
    ) {
        load(resourceId as Any, iv, cornerRadius, cornerType, blurRadius, enableFadeTransition, placeHolderResId, placeHolderDrawable, fallbackResId, fallbackDrawable)
    }

    fun load(
        url: String?,
        iv: ImageView,
        cornerRadius: Int = 0,
        cornerType: RoundedCornersTransformation.CornerType? = null,
        blurRadius: Int = 0,
        enableFadeTransition: Boolean = true,
        @DrawableRes placeHolderResId: Int? = null,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes fallbackResId: Int? = null,
        fallbackDrawable: Drawable? = null
    ) {
        load(url as? Any, iv, cornerRadius, cornerType, blurRadius, enableFadeTransition, placeHolderResId, placeHolderDrawable, fallbackResId, fallbackDrawable)
    }

    private fun load(
        source: Any?,
        iv: ImageView,
        cornerRadius: Int = 0,
        cornerType: RoundedCornersTransformation.CornerType? = null,
        blurRadius: Int = 0,
        enableFadeTransition: Boolean = true,
        @DrawableRes placeHolderResId: Int? = null,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes fallbackResId: Int? = null,
        fallbackDrawable: Drawable? = null
    ) {
        if (isDestroy(context)) return
        if (source != null && source !is String && source !is Int) return
        val requestBuilder = when (source) {
            is String -> requestManager.load(source)
            is Int -> requestManager.load(source)
            else -> requestManager.load(source as? String)
        }
        requestBuilder.apply {
            if (enableFadeTransition) this.transition(DrawableTransitionOptions.withCrossFade())
            if (placeHolderResId != null) this.placeholder(placeHolderResId)
            if (placeHolderDrawable != null) this.placeholder(placeHolderDrawable)
            if (fallbackResId != null) this.fallback(fallbackResId)
            if (fallbackDrawable != null) this.placeholder(fallbackDrawable)
        }.apply {
            // apply corner
            val hasCornerRadius = cornerRadius > 0
            var transformations: ArrayList<Transformation<Bitmap>>? = null

            // apply blur
            if (blurRadius > 0) {
                if (transformations == null) transformations = arrayListOf()
                transformations.add(BlurTransformation(blurRadius))
            }

            // corner
            if (hasCornerRadius) {
                if (transformations == null) transformations = arrayListOf()
                transformations.add(RoundedCornersTransformation(cornerRadius.dp.toInt(), 0, cornerType ?: RoundedCornersTransformation.CornerType.ALL))
            }

            if (transformations != null) this.apply(bitmapTransform(MultiTransformation(transformations)))
        }.into(iv)
    }

    fun loadAvatar(url: String?, iv: ImageView) {
        if (isDestroy(context)) return
        requestManager.load(processInvalidAvatar(url)).transition(DrawableTransitionOptions.withCrossFade()).apply(avatarOption).into(iv)
    }

    fun loadCircle(
        url: String?,
        iv: ImageView,
        blurRadius: Int = 0,
        enableFadeTransition: Boolean = true,
        @DrawableRes placeHolderResId: Int? = null,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes fallbackResId: Int? = null,
        fallbackDrawable: Drawable? = null
    ) {
        loadCircle(url as? Any, iv, blurRadius, enableFadeTransition, placeHolderResId, placeHolderDrawable, fallbackResId, fallbackDrawable)
    }

    fun loadCircle(
        @DrawableRes resourceId: Int,
        iv: ImageView,
        blurRadius: Int = 0,
        enableFadeTransition: Boolean = true,
        @DrawableRes placeHolderResId: Int? = null,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes fallbackResId: Int? = null,
        fallbackDrawable: Drawable? = null
    ) {
        loadCircle(resourceId as? Any, iv, blurRadius, enableFadeTransition, placeHolderResId, placeHolderDrawable, fallbackResId, fallbackDrawable)
    }

    private fun loadCircle(
        source: Any?,
        iv: ImageView,
        blurRadius: Int = 0,
        enableFadeTransition: Boolean = true,
        @DrawableRes placeHolderResId: Int? = null,
        placeHolderDrawable: Drawable? = null,
        @DrawableRes fallbackResId: Int? = null,
        fallbackDrawable: Drawable? = null
    ) {
        if (isDestroy(context)) return
        if (source != null && source !is String && source !is Int) return
        val requestBuilder = when (source) {
            is String -> requestManager.load(source)
            is Int -> requestManager.load(source)
            else -> requestManager.load(source as? String)
        }
        requestBuilder.apply {
            if (enableFadeTransition) this.transition(DrawableTransitionOptions.withCrossFade())
            if (placeHolderResId != null) this.placeholder(placeHolderResId)
            if (placeHolderDrawable != null) this.placeholder(placeHolderDrawable)
            if (fallbackResId != null) this.fallback(fallbackResId)
            if (fallbackDrawable != null) this.placeholder(fallbackDrawable)
        }.apply {
            var transformations: ArrayList<Transformation<Bitmap>>? = null
            // apply blur
            if (blurRadius > 0) {
                if (transformations == null) transformations = arrayListOf()
                transformations.add(BlurTransformation(blurRadius))
            }
            if (transformations != null) this.apply(bitmapTransform(MultiTransformation(transformations)))
        }.apply(circleOption).into(iv)
    }

    private fun processInvalidAvatar(url: String?): String? {
        if (url == null) return null
        if (invalidAvatars.contains(url)) return null
        return url
    }

    private fun isDestroy(context: Context?): Boolean {
        if (context == null) return true
        if (context is Activity && context.isFinishing) return true
        return false
    }
}
