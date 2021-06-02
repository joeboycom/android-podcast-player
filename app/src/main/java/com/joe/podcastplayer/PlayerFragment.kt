package com.joe.podcastplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.PlayerFragmentBinding
import io.reactivex.rxjava3.disposables.Disposable

class PlayerFragment : BaseFragment<PlayerFragmentBinding>() {

    companion object {
        const val TAG = "AudioPlayerFragment"
        const val POS_COVER = 0
        const val POS_DESCRIPTION = 1
        private const val NUM_CONTENT_FRAGMENTS = 2
        private const val EPSILON = 0.001f

        private const val BUNDLE_EPISODE_TITLE = "BUNDLE_EPISODE_TITLE"
        private const val BUNDLE_EPISODE_IMAGE_URL = "BUNDLE_EPISODE_IMAGE_URL"
        fun newInstance(episodeTitle: String?, episodeImageUrl: String?): PlayerFragment = PlayerFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_EPISODE_TITLE, episodeTitle)
            bundle.putString(BUNDLE_EPISODE_IMAGE_URL, episodeImageUrl)
            arguments = bundle
        }
    }

    private var episodeTitle = ""
    private var episodeImageUrl = ""
    private val controller: PlaybackController? = null
    private val disposable: Disposable? = null
    private val showTimeLeft = false
    private val seekedToChapterStart = false
    private val currentChapterIndex = -1
    private val duration = 0

    override fun enableEventBus(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initName() {
        // set screen name
    }

    override fun initIntent() {
        val bundle = arguments
        episodeTitle = bundle?.getString(BUNDLE_EPISODE_TITLE, "") ?: ""
        episodeImageUrl = bundle?.getString(BUNDLE_EPISODE_IMAGE_URL, "") ?: ""
    }

    override fun init() {

    }

    override fun initLayout() {
//        viewBinding.episodeTitleTextView.text = episodeTitle
//        imageLoader.load(episodeImageUrl, viewBinding.headerImageView)
    }

    override fun initAction() {
//        viewBinding.playAppCompatButton.onClick {
//            val fragment = AudioPlayerFragment()
//            (activity as MainActivity?)!!.loadChildFragment(fragment, TransitionEffect.SLIDE)
//        }
    }

    override fun initObserver() {
    }

}
