package com.joe.podcastplayer.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.joe.podcastplayer.extension.className
import com.joe.podcastplayer.extension.gson
import com.joe.podcastplayer.extension.onClick
import com.joe.podcastplayer.service.di.InjectorUtils
import com.joe.podcastplayer.viewModel.EpisodeViewModel
import com.joe.podcastplayer.viewModel.NowPlayingViewModel
import com.joe.podcastplayer.viewModel.NowPlayingViewModel.NowPlayingMetadata.Companion.timestampToMSS
import com.prof.rssparser.FeedItem


class NowPlayingFragment : BaseFragment<NowPlayingFragmentBinding>() {
    companion object {
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
        viewBinding.largePlayer.largeTitle.isFocusable = true
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
            nowPlayingViewModel.mediaMetadataMutableLiveData.value?.let { episodeViewModel.playMedia(feedItem) }
        }

        viewBinding.largePlayer.largePlayPauseButton.setOnClickListener {
            episodeViewModel.playMedia(feedItem!!)
            nowPlayingViewModel.mediaMetadataMutableLiveData.value?.let { episodeViewModel.playMedia(feedItem) }
        }

        viewBinding.largePlayer.largePreviousButton.setOnClickListener {
            nowPlayingViewModel.skipToPrevious()
        }

        viewBinding.largePlayer.largeNextButton.setOnClickListener {
            nowPlayingViewModel.skipToNext()
        }

        viewBinding.largePlayer.rewindButton.setOnClickListener {
            nowPlayingViewModel.rewind()
        }

        viewBinding.largePlayer.fastFowardButton.setOnClickListener {
            nowPlayingViewModel.fastForward()
        }

        viewBinding.largePlayer.ivClose.onClick {
        }
    }

    override fun initObserver() {
        // Attach observers to the LiveData coming from this ViewModel
        nowPlayingViewModel.mediaMetadataMutableLiveData.observe(viewLifecycleOwner,
            { mediaItem -> updateUI(view, mediaItem) })

        nowPlayingViewModel.mediaPlayProgressMutableLiveData.observe(viewLifecycleOwner,
            { progress -> updateProgressBar(progress) })

        nowPlayingViewModel.mediaPositionMutableLiveData.observe(viewLifecycleOwner,
            { position -> viewBinding.largePlayer.nowDuration.text = timestampToMSS(requireContext(), position) })

        nowPlayingViewModel.mediaButtonResMutableLiveData.observe(viewLifecycleOwner,
            {
                viewBinding.smallPlayer.playPauseImage.setImageState(it, true)
                viewBinding.largePlayer.largePlayPauseButton.setImageState(it, true)
            })
    }

    private fun setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheetNowPlayingFragmentLayout)
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d(className, "slideOffset:$slideOffset")
                viewBinding.smallPlayer.sectionNowPlayingSmall.alpha = 1 - slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> Log.d(className, "STATE_COLLAPSED")
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d(className, "STATE_DRAGGING")
                        viewBinding.smallPlayer.sectionNowPlayingSmall.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d(className, "STATE_EXPANDED")
                        viewBinding.smallPlayer.sectionNowPlayingSmall.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> Log.d(className, "STATE_HIDDEN")
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> Log.d(className, "STATE_HALF_EXPANDED")
                    BottomSheetBehavior.STATE_SETTLING -> Log.d(className, "STATE_SETTLING")
                }
            }
        })
    }

    private fun updateUI(view: View?, metadata: NowPlayingViewModel.NowPlayingMetadata) {
        if (view == null) return
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
        viewBinding.smallPlayer.smallSubTitle.text = channelTitle
        viewBinding.largePlayer.largeTitle.text = metadata.title
        viewBinding.largePlayer.largeSubTitle.text = channelTitle
        viewBinding.largePlayer.totalDuration.text = metadata.duration
    }

    private fun updateProgressBar(progress: Int) {
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