package com.joe.podcastplayer

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.EpisodeFragmentBinding
import com.joe.podcastplayer.extension.className
import com.joe.podcastplayer.extension.gson
import com.joe.podcastplayer.extension.onClick
import com.joe.podcastplayer.extension.toJson
import com.joe.podcastplayer.service.di.InjectorUtils
import com.joe.podcastplayer.service.ui.nowplaying.NowPlayingFragment
import com.joe.podcastplayer.service.ui.song.EpisodeViewModel
import com.prof.rssparser.FeedItem

class EpisodeFragment : BaseFragment<EpisodeFragmentBinding>() {

    companion object {
        private const val BUNDLE_CHANNEL_TITLE = "BUNDLE_CHANNEL_TITLE"
        private const val BUNDLE_FEED_ITEM = "BUNDLE_FEED_ITEM"
        fun newInstance(channelTitle: String?, feedItem: String?): EpisodeFragment = EpisodeFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_CHANNEL_TITLE, channelTitle)
            bundle.putString(BUNDLE_FEED_ITEM, feedItem)
            arguments = bundle
        }
    }

    private val episodeViewModel: EpisodeViewModel by viewModels() {
        InjectorUtils.provideSongListViewModel(requireContext())
    }
    private val handler = Handler()
    private var channelTitle = ""
    private var feedItem: FeedItem? = null

    override fun enableEventBus(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initName() {
        // set screen name
    }

    override fun initIntent() {
        val bundle = arguments
        channelTitle = bundle!!.getString(BUNDLE_CHANNEL_TITLE, "")
        feedItem = gson.fromJson(bundle.getString(BUNDLE_FEED_ITEM, ""), FeedItem::class.java)
        Log.e(className, "feedItem:$feedItem")
    }

    override fun init() {

    }

    override fun initLayout() {
        viewBinding.episodeChannelTextView.text = channelTitle
        viewBinding.tvEpisodeTitle.text = feedItem?.title
        viewBinding.episodeDescriptionTextView.text = feedItem?.description

        imageLoader.load(feedItem?.image, viewBinding.ivHeaderImage)
    }

    override fun initAction() {
        viewBinding.playAppCompatButton.onClick {
            if (feedItem == null) return@onClick
            val fragment = NowPlayingFragment.newInstance(feedItem!!.toJson())
            (activity as MainActivity?)!!.loadChildFragment(fragment, TransitionEffect.SLIDE)

            playPodcast(feedItem!!)
        }
    }

    override fun initObserver() {
    }

    private fun playPodcast(feedItem: FeedItem) {
        handler.postDelayed({
            episodeViewModel.playMedia(feedItem)
        }, 1000)
    }
}
