package com.joe.podcastplayer

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.PlayerFragmentBinding
import com.joe.podcastplayer.extension.gson
import com.joe.podcastplayer.extension.onClick
import com.prof.rssparser.FeedItem
import io.reactivex.rxjava3.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import java.text.NumberFormat

class PlayerFragment : BaseFragment<PlayerFragmentBinding>() {

    companion object {
        const val TAG = "AudioPlayerFragment"
        const val POS_COVER = 0
        const val POS_DESCRIPTION = 1
        private const val NUM_CONTENT_FRAGMENTS = 2
        private const val EPSILON = 0.001f

        private const val BUNDLE_FEED_ITEM = "BUNDLE_FEED_ITEM"
        fun newInstance(feedItem: String?): PlayerFragment = PlayerFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_FEED_ITEM, feedItem)
            arguments = bundle
        }
    }

    private var feedItem: FeedItem? = null
    private var controller: PlaybackController? = null
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
        feedItem = gson.fromJson(bundle!!.getString(BUNDLE_FEED_ITEM, ""), FeedItem::class.java)
    }

    override fun init() {

    }

    override fun initLayout() {
        viewBinding.tvEpisodeTitle.text = feedItem?.title
        imageLoader.load(feedItem?.image, viewBinding.ivHeaderImage)
    }

    override fun initAction() {
        viewBinding.butPlay.onClick {
            if (controller != null) {
                controller!!.init()
                controller!!.playPause()
            }
        }
    }

    override fun initObserver() {
    }

    override fun onStart() {
        super.onStart()
        controller = newPlaybackController()
        controller!!.init()
//        loadMediaInfo()
    }

    private fun newPlaybackController(): PlaybackController {
        return object : PlaybackController(baseActivity!!) {
            override fun onBufferStart() {
                viewBinding.progressIndicator.setVisibility(View.VISIBLE)
            }

            override fun onBufferEnd() {
                viewBinding.progressIndicator.setVisibility(View.GONE)
            }

            override fun onBufferUpdate(progress: Float) {
                if (isStreaming) {
                    viewBinding.sbPosition.setSecondaryProgress((progress * viewBinding.sbPosition.getMax()) as Int)
                } else {
                    viewBinding.sbPosition.setSecondaryProgress(0)
                }
            }

            override fun handleError(code: Int) {
            }

            override fun onSleepTimerUpdate() {
//                this@AudioPlayerFragment.loadMediaInfo()
            }

            override fun updatePlayButtonShowsPlay(showPlay: Boolean) {
                viewBinding.butPlay.setIsShowPlay(showPlay)
            }

            override fun loadMediaInfo() {
//                this@AudioPlayerFragment.loadMediaInfo()
            }

            override fun onPlaybackEnd() {
//                (activity as MainActivity?).getBottomSheet().setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
    }
}
