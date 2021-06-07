package com.joe.podcastplayer.viewModel

import android.view.ViewGroup
import com.joe.podcastplayer.R
import com.joe.podcastplayer.base.BaseViewHolder
import com.joe.podcastplayer.databinding.HolderItemEpisodeBinding
import com.joe.podcastplayer.extension.onClick
import com.prof.rssparser.FeedItem
import java.text.SimpleDateFormat
import java.util.*

class EpisodeItemViewHolder(parent: ViewGroup) : BaseViewHolder<HolderItemEpisodeBinding>(fetchViewBinding(parent)) {
    companion object {
        fun newInstance(parent: ViewGroup) = EpisodeItemViewHolder(parent)
    }

    private var onClickListener: ((article: FeedItem) -> Unit)? = null
    private var feedItem: FeedItem? = null

    init {
        viewBinding.cardView.onClick {
            if (feedItem != null) {
                onClickListener?.invoke(feedItem!!)
            }
        }
    }

    fun bind(feedItem: FeedItem) {
        this.feedItem = feedItem
        if (this.feedItem == null) return
        viewBinding.tvEpisodeTitle.text = feedItem.title
        viewBinding.tvEpisodeDescription.text = feedItem.description
        imageLoader.load(url = feedItem.image, iv = viewBinding.ivEpisodeImage, cornerRadius = 0, placeHolderResId = R.mipmap.placeholder)
        viewBinding.tvPublishingDate.text = getPublishingDateText(feedItem.pubDate)
    }

    private fun getPublishingDateText(publishingDateText: String?): String {
        try {
            val sourceSdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
            if (publishingDateText != null) {
                val date = sourceSdf.parse(publishingDateText)
                if (date != null) {
                    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    return sdf.format(date)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun setOnClickListener(listener: ((article: FeedItem) -> Unit)?) {
        onClickListener = listener
    }
}
