package com.joe.podcastplayer.utility

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.request.RequestOptions

class GlideModule {
    companion object {
        // https://bumptech.github.io/glide/doc/configuration.html
        // Libraries must not include AppGlideModule implementations.
        fun applyOptions(context: Context, builder: GlideBuilder) {
            val memoryCacheSizeBytes = 1024L * 1024L * 20L // 20mb
            val diskCacheSizeBytes = 1024L * 1024L * 100L // 100mb
            builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes))
            builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes))
            builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
        }
    }
}
