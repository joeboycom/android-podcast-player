package com.joe.podcastplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.EpisodeFragmentBinding
import com.joe.podcastplayer.extension.onClick

class EpisodeFragment : BaseFragment<EpisodeFragmentBinding>() {

    companion object {
        private const val BUNDLE_CHANNEL_TITLE = "BUNDLE_CHANNEL_TITLE"
        private const val BUNDLE_EPISODE_TITLE = "BUNDLE_EPISODE_TITLE"
        private const val BUNDLE_EPISODE_IMAGE_URL = "BUNDLE_EPISODE_IMAGE_URL"
        private const val BUNDLE_EPISODE_DESCRIPTION = "BUNDLE_EPISODE_DESCRIPTION"
        fun newInstance(channelTitle: String?, episodeTitle: String?, episodeImageUrl: String?, episodeDescription: String?): EpisodeFragment = EpisodeFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_CHANNEL_TITLE, channelTitle)
            bundle.putString(BUNDLE_EPISODE_TITLE, episodeTitle)
            bundle.putString(BUNDLE_EPISODE_IMAGE_URL, episodeImageUrl)
            bundle.putString(BUNDLE_EPISODE_DESCRIPTION, episodeDescription)
            arguments = bundle
        }
    }

    private var channelTitle = ""
    private var episodeTitle = ""
    private var episodeImageUrl = ""
    private var episodeDescription = ""

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
        episodeTitle = bundle.getString(BUNDLE_EPISODE_TITLE, "")
        episodeImageUrl = bundle.getString(BUNDLE_EPISODE_IMAGE_URL, "")
        episodeDescription = bundle.getString(BUNDLE_EPISODE_DESCRIPTION, "")

        Log.e("HAHA", "$channelTitle $episodeDescription")
    }

    override fun init() {

    }

    override fun initLayout() {
        viewBinding.episodeChannelTextView.text = channelTitle
        viewBinding.episodeTitleTextView.text = episodeTitle
        viewBinding.episodeDescriptionTextView.text = episodeDescription

        imageLoader.load(episodeImageUrl, viewBinding.headerImageView)
    }

    override fun initAction() {
        viewBinding.playAppCompatButton.onClick {
//            val fragment = StatisticsFragment()
//            (activity as MainActivity?)!!.loadChildFragment(fragment, TransitionEffect.SLIDE)
        }
    }

    override fun initObserver() {
    }

}
