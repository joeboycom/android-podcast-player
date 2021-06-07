package com.joe.podcastplayer.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.joe.podcastplayer.R
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.NowPlayingFragmentBinding
import com.joe.podcastplayer.extension.gson
import com.joe.podcastplayer.service.di.InjectorUtils
import com.joe.podcastplayer.viewModel.EpisodeViewModel
import com.joe.podcastplayer.viewModel.NowPlayingViewModel
import com.joe.podcastplayer.viewModel.NowPlayingViewModel.NowPlayingMetadata.Companion.timestampToMSS
import com.prof.rssparser.FeedItem


class NowPlayingFragment : BaseFragment<NowPlayingFragmentBinding>() {
    companion object {
        const val TAG = "NowPlayingFragment"
        private const val BUNDLE_CHANNEL_TITLE = "BUNDLE_CHANNEL_TITLE"
        private const val BUNDLE_FEED_ITEM = "BUNDLE_FEED_ITEM"
        fun newInstance(channelTitle: String?, feedItem: String?): NowPlayingFragment = NowPlayingFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_CHANNEL_TITLE, channelTitle)
            bundle.putString(BUNDLE_FEED_ITEM, feedItem)
            arguments = bundle
        }
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var seekbarScrollingStart = false
    private var channelTitle = ""
    private var feedItem: FeedItem? = null

    private val episodeViewModel: EpisodeViewModel by viewModels {
        InjectorUtils.provideFeedItemListViewModel(requireContext())
    }

    private val nowPlayingViewModel: NowPlayingViewModel by viewModels {
        InjectorUtils.provideNowPlayingViewModel(requireContext())
    }

    override fun initName() {
    }

    override fun initIntent() {
        val bundle = arguments
        channelTitle = bundle!!.getString(BUNDLE_CHANNEL_TITLE, "")
        feedItem = gson.fromJson(bundle.getString(BUNDLE_FEED_ITEM, ""), FeedItem::class.java)
    }

    override fun init() {

    }

    override fun initLayout() {
        setBottomSheetBehavior()
        disableSeekInSmallSeekBar()

        viewBinding.largePlayer.tvChannelTitle.text = channelTitle
    }

    override fun initAction() {
        viewBinding.largePlayer.largeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekbarScrollingStart = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekbarScrollingStart) {
                    nowPlayingViewModel.changePlaybackPosition(seekBar!!.progress, seekBar.max)
                    seekbarScrollingStart = false
                }
            }
        })

        viewBinding.smallPlayer.playPauseImage.setOnClickListener {
            nowPlayingViewModel.mediaMetadata.value?.let { episodeViewModel.playMedia(feedItem) }
        }

        viewBinding.largePlayer.largePlayPauseButton.setOnClickListener {
            episodeViewModel.playMedia(feedItem!!)
            nowPlayingViewModel.mediaMetadata.value?.let { episodeViewModel.playMedia(feedItem) }
        }

        viewBinding.largePlayer.largePreviousButton.setOnClickListener {
            nowPlayingViewModel.skipToPreviousSong()
        }

        viewBinding.largePlayer.largeNextButton.setOnClickListener {
            nowPlayingViewModel.skipToNextSong()
        }

        viewBinding.largePlayer.shuffleButton.setOnClickListener {
            nowPlayingViewModel.changeShuffleMode()
        }

        viewBinding.largePlayer.repeatButton.setOnClickListener {
            nowPlayingViewModel.changeRepeatMode()
        }
    }

    override fun initObserver() {
        // Attach observers to the LiveData coming from this ViewModel
        nowPlayingViewModel.mediaMetadata.observe(viewLifecycleOwner,
            { mediaItem -> updateUI(view, mediaItem) })

        nowPlayingViewModel.mediaPlayProgress.observe(viewLifecycleOwner,
            { progress -> updateProgressBar(progress) })

        nowPlayingViewModel.mediaPosition.observe(viewLifecycleOwner,
            { position -> viewBinding.largePlayer.nowDuration.text = timestampToMSS(requireContext(), position) })

        nowPlayingViewModel.mediaButtonRes.observe(viewLifecycleOwner,
            {
                viewBinding.smallPlayer.playPauseImage.setImageState(it, true)
                viewBinding.largePlayer.largePlayPauseButton.setImageState(it, true)
            })

        nowPlayingViewModel.shuffleMode.observe(viewLifecycleOwner,
            {
                when (it) {
                    PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                        viewBinding.largePlayer.shuffleButton.setColorFilter(Color.WHITE)
                    }
                    PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                        viewBinding.largePlayer.shuffleButton.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                    }
                }
            }
        )

        nowPlayingViewModel.repeatMode.observe(viewLifecycleOwner,
            {
                when (it) {
                    PlaybackStateCompat.REPEAT_MODE_NONE -> {
                        viewBinding.largePlayer.repeatButton.setImageResource(R.drawable.ic_repeat)
                        viewBinding.largePlayer.repeatButton.setColorFilter(Color.WHITE)
                    }
                    PlaybackStateCompat.REPEAT_MODE_ONE -> {
                        viewBinding.largePlayer.repeatButton.setImageResource(R.drawable.ic_repeat_one)
                        viewBinding.largePlayer.repeatButton.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                    }
                    PlaybackStateCompat.REPEAT_MODE_ALL -> {
                        viewBinding.largePlayer.repeatButton.setImageResource(R.drawable.ic_repeat)
                        viewBinding.largePlayer.repeatButton.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorPrimary
                            )
                        )
                    }
                }
            }
        )
    }

    private fun setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheetNowPlayingFragmentLayout)
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d(TAG, "slideOffset:$slideOffset")
                viewBinding.smallPlayer.sectionNowPlayingSmall.alpha = 1 - slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> Log.d(TAG, "STATE_COLLAPSED")
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d(TAG, "STATE_DRAGGING")
                        viewBinding.smallPlayer.sectionNowPlayingSmall.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d(TAG, "STATE_EXPANDED")
                        viewBinding.smallPlayer.sectionNowPlayingSmall.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> Log.d(TAG, "STATE_HIDDEN")
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> Log.d(TAG, "STATE_HALF_EXPANDED")
                    BottomSheetBehavior.STATE_SETTLING -> Log.d(TAG, "STATE_SETTLING")
                }
            }
        })
    }

    private fun updateUI(view: View?, metadata: NowPlayingViewModel.NowPlayingMetadata) {
        Log.e("HAHA-----", "updateUI")
        if (view == null) return
        Log.e("HAHA", "${metadata.albumArtUri}")
        Log.e("HAHA", "${metadata.duration}")
        Log.e("HAHA", "${metadata.id}")
        Log.e("HAHA", "${metadata.title}")
        Log.e("HAHA", "${metadata.subtitle}")
        if (metadata.albumArtUri == Uri.EMPTY) {
            viewBinding.smallPlayer.smallCover.setImageResource(R.drawable.ic_default_cover_icon)
            viewBinding.smallPlayer.smallCover.setBackgroundResource(R.drawable.ic_default_cover_background)
            viewBinding.largePlayer.largeCover.setImageResource(R.drawable.ic_default_cover_icon)
            viewBinding.largePlayer.largeCover.setBackgroundResource(R.drawable.ic_default_cover_background)


        } else {
            Glide.with(view)
                .load(metadata.albumArtUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewBinding.smallPlayer.smallCover)

            Glide.with(view)
                .load(metadata.albumArtUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewBinding.largePlayer.largeCover)

            Glide.with(view)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(metadata.albumArtUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        //Nothing
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        Palette.from(resource).generate { palette ->
                            if (palette == null) return@generate
                            setTitleColor(palette)
                        }
                    }
                })
        }

        viewBinding.smallPlayer.smallTitle.text = metadata.title
        viewBinding.smallPlayer.smallSubTitle.text = metadata.subtitle
        viewBinding.largePlayer.largeTitle.text = metadata.title
        viewBinding.largePlayer.totalDuration.text = metadata.duration
    }

    private fun updateProgressBar(progress: Int) {
        Log.e("HAHA-----", "updateProgressBar $seekbarScrollingStart $progress")
        if (seekbarScrollingStart) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewBinding.smallPlayer.smallSeekBar.setProgress(progress, true)
            viewBinding.largePlayer.largeSeekBar.setProgress(progress, true)
        } else {
            viewBinding.smallPlayer.smallSeekBar.progress = progress
            viewBinding.largePlayer.largeSeekBar.progress = progress
        }
    }

    private fun disableSeekInSmallSeekBar() {
        viewBinding.smallPlayer.smallSeekBar.setOnTouchListener { _, _ -> true }
    }

    private fun setTitleColor(palette: Palette) {
        val bodyColor: Int = palette.getDominantColor(
            ContextCompat.getColor(requireContext(), android.R.color.black)
        )
    }
}