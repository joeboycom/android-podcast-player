package com.joe.podcastplayer.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.google.gson.reflect.TypeToken
import com.joe.podcastplayer.constant.TransitionEffect
import com.joe.podcastplayer.activity.MainActivity
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.EpisodeFragmentBinding
import com.joe.podcastplayer.extension.*
import com.joe.podcastplayer.utility.InjectorUtils
import com.joe.podcastplayer.viewModel.MainActivityViewModel
import com.prof.rssparser.FeedItem

class EpisodeFragment : BaseFragment<EpisodeFragmentBinding>() {

    companion object {
        private const val BUNDLE_CHANNEL_TITLE = "BUNDLE_CHANNEL_TITLE"
        private const val BUNDLE_FEED_ITEM = "BUNDLE_FEED_ITEM"
        private const val BUNDLE_FEED_ITEM_LISE = "BUNDLE_FEED_ITEM_LISE"
        fun newInstance(channelTitle: String?, feedItem: String?, feedItemList: String?): EpisodeFragment = EpisodeFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_CHANNEL_TITLE, channelTitle)
            bundle.putString(BUNDLE_FEED_ITEM, feedItem)
            bundle.putString(BUNDLE_FEED_ITEM_LISE, feedItemList)
            arguments = bundle
        }
    }

    private val viewModel: MainActivityViewModel by viewModels {
        InjectorUtils.provideMainActivityViewModel(requireContext())
    }
    private val handler = Handler()
    private var channelTitle = ""
    private var currentMetadata: MediaMetadataCompat? = null
    private var feedItem: FeedItem? = null
    private var feedItemList: ArrayList<FeedItem>? = null

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
        feedItemList = gson.fromJson(bundle.getString(BUNDLE_FEED_ITEM_LISE), object : TypeToken<ArrayList<FeedItem>>() {}.type)
        currentMetadata = feedItem?.toMediaMetadataCompat()
        Log.e(className, "feedItem:$feedItem")
    }

    override fun init() {
        val pref = baseActivity!!.getSharedPreferences("podcast_player", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("pref_feed_item_list", feedItemList?.toJson()).apply()
    }

    override fun initLayout() {
        viewBinding.tvEpisodeChannel.text = channelTitle
        viewBinding.tvEpisodeTitle.text = feedItem?.title
        viewBinding.tvEpisodeDescription.text = feedItem?.description
        viewBinding.ivHeaderImage.load(feedItem?.image)
    }

    override fun initAction() {
        viewBinding.playAppCompatButton.onClick {
            if (currentMetadata == null) return@onClick
            val fragment = NowPlayingFragment.newInstance(channelTitle, feedItem!!.toJson())
            (activity as MainActivity?)!!.showFragment(fragment, TransitionEffect.SLIDE)

            playPodcast()
        }

        viewBinding.ivBack.onClick {
            activity?.onBackPressed()
        }
    }

    override fun initObserver() {
    }

    private fun playPodcast() {
        if (feedItem == null) return
        handler.postDelayed({
            viewModel.playMedia(feedItem!!.toNowPlayingMetadata())
        }, 1000)
    }
}
