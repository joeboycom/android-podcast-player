package com.joe.podcastplayer.base

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.joe.podcastplayer.utility.ImageLoader
import com.joe.podcastplayer.extension.activity
import com.joe.podcastplayer.extension.baseActivity
import com.joe.podcastplayer.extension.layoutInflater
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

interface ICoreViewHolder {
    val currentPosition: Int?
}

open class BaseViewHolder<T : ViewBinding>(protected val viewBinding: T) : RecyclerView.ViewHolder(viewBinding.root), ICoreViewHolder {

    companion object {
        inline fun <reified T : ViewBinding> fetchViewBinding(parent: ViewGroup): T {
            val method = T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
            return method.invoke(null, parent.layoutInflater, parent, false) as T
        }
    }

    val baseActivity get() = itemView.baseActivity
    val activity get() = itemView.activity
    private val imageLoader: ImageLoader by lazy { ImageLoader(baseActivity!!) }

    override val currentPosition: Int?
        get() {
            val position = adapterPosition
            if (position < 0) return null
            return position
        }

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
