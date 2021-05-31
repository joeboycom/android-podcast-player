package com.joe.podcastplayer.base

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.joe.podcastplayer.ImageLoader
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
    val imageLoader: ImageLoader by lazy { ImageLoader(baseActivity!!) }

    override val currentPosition: Int?
        get() {
            val position = adapterPosition
            if (position < 0) return null
            return position
        }
}
