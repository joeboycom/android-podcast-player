package com.joe.podcastplayer

import android.util.Log
import android.view.ViewGroup
import com.joe.podcastplayer.base.BaseViewHolder
import com.joe.podcastplayer.databinding.HolderItemEpisodeBinding
import com.joe.podcastplayer.extension.onClick
import com.prof.rssparser.Article
import java.text.SimpleDateFormat
import java.util.*

class EpisodeItemViewHolder(parent: ViewGroup) : BaseViewHolder<HolderItemEpisodeBinding>(fetchViewBinding(parent)) {
    companion object {
        fun newInstance(parent: ViewGroup) = EpisodeItemViewHolder(parent)
    }

    private var onClickListener: ((article: Article) -> Unit)? = null
    private var article: Article? = null

    init {
        viewBinding.cardView.onClick {
            if (article != null) {
                onClickListener?.invoke(article!!)
            }
        }
    }

    fun bind(article: Article) {
        this.article = article
        var pubDateString = article.pubDate

        try {
            val sourceDateString = article.pubDate
            val sourceSdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
            if (sourceDateString != null) {
                val date = sourceSdf.parse(sourceDateString)
                if (date != null) {
                    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    pubDateString = sdf.format(date)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        viewBinding.title.text = article.title
        imageLoader.load(url = article.image, iv = viewBinding.image, cornerRadius = 4, placeHolderResId = R.mipmap.placeholder)
        viewBinding.pubDate.text = pubDateString
    }

    fun setOnClickListener(listener: ((article: Article) -> Unit)?) {
        onClickListener = listener
    }
}
