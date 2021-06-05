package com.joe.podcastplayer.service.ui.nowplaying

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.joe.podcastplayer.R
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.NowPlayingFragmentBinding
import com.joe.podcastplayer.service.di.InjectorUtils
import com.joe.podcastplayer.service.ui.nowplaying.NowPlayingViewModel.NowPlayingMetadata.Companion.timestampToMSS
import com.joe.podcastplayer.service.ui.song.SongListViewModel


class NowPlayingFragment : BaseFragment<NowPlayingFragmentBinding>() {
    companion object {
        const val TAG = "NowPlayingFragment"
        fun newInstance() = NowPlayingFragment()
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var seekbarScrollingStart = false

    private val songListViewModel: SongListViewModel by viewModels {
        InjectorUtils.provideSongListViewModel(requireContext())
    }

    private val nowPlayingViewModel: NowPlayingViewModel by viewModels {
        InjectorUtils.provideNowPlayingViewModel(requireContext())
    }

    override fun initName() {

    }

    override fun initIntent() {

    }

    override fun init() {

    }

    override fun initLayout() {
        setBottomSheetBehavior()
        disableSeekInSmallSeekBar()

        viewBinding.largePlayer.largeSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.e("HAHA", "onProgressChanged : $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.e("HAHA", "onStartTrackingTouch")
                seekbarScrollingStart = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.e("HAHA", "onStopTrackingTouch $seekbarScrollingStart")
                if (seekbarScrollingStart) {
                    nowPlayingViewModel.changePlaybackPosition(seekBar!!.progress, seekBar.max)
                    seekbarScrollingStart = false
                }
            }
        })

        // Attach observers to the LiveData coming from this ViewModel
        nowPlayingViewModel.mediaMetadata.observe(viewLifecycleOwner,
            Observer { mediaItem -> updateUI(view, mediaItem) })

        nowPlayingViewModel.mediaPlayProgress.observe(viewLifecycleOwner,
            Observer { progress -> updateProgressBar(progress) })

        nowPlayingViewModel.mediaPosition.observe(viewLifecycleOwner,
            Observer { position -> viewBinding.largePlayer.nowDuration.text = timestampToMSS(requireContext(), position) })

        nowPlayingViewModel.mediaButtonRes.observe(viewLifecycleOwner,
            Observer {
                viewBinding.smallPlayer.playPauseImage.setImageState(it, true)
                viewBinding.largePlayer.largePlayPauseButton.setImageState(it, true)
            })

        nowPlayingViewModel.shuffleMode.observe(viewLifecycleOwner,
            Observer {
                when (it) {
                    PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                        viewBinding.largePlayer.shuffleButton.setColorFilter(Color.BLACK)
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
            Observer {
                when (it) {
                    PlaybackStateCompat.REPEAT_MODE_NONE -> {
                        viewBinding.largePlayer.repeatButton.setImageResource(R.drawable.ic_repeat)
                        viewBinding.largePlayer.repeatButton.setColorFilter(Color.BLACK)
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


        viewBinding.smallPlayer.playPauseImage.setOnClickListener {
            nowPlayingViewModel.mediaMetadata.value?.let { songListViewModel.playMediaId(it.id) }
        }

        viewBinding.largePlayer.largePlayPauseButton.setOnClickListener {
            nowPlayingViewModel.mediaMetadata.value?.let { songListViewModel.playMediaId(it.id) }
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

    override fun initAction() {

    }

    override fun initObserver() {

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
        if (view == null) return
        if (metadata.albumArtUri == Uri.EMPTY) {
            viewBinding.smallPlayer.smallCover.setImageResource(R.drawable.ic_default_cover_icon)
            viewBinding.smallPlayer.smallCover.setBackgroundResource(R.drawable.ic_default_cover_background)
            viewBinding.largePlayer.largeCover.setImageResource(R.drawable.ic_default_cover_icon)
            viewBinding.largePlayer.largeCover.setBackgroundResource(R.drawable.ic_default_cover_background)

            viewBinding.largePlayer.titleBackground.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )

            viewBinding.largePlayer.largeTitle.setTextColor(Color.WHITE)
            viewBinding.largePlayer.largeSubTitle.setTextColor(Color.WHITE)

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
        viewBinding.largePlayer.largeSubTitle.text = metadata.subtitle
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

        val titleTextColor =
            palette.getLightVibrantColor(
                ContextCompat.getColor(requireContext(), android.R.color.white)
            )

        val bodyTextColor =
            palette.getLightMutedColor(
                ContextCompat.getColor(requireContext(), android.R.color.white)
            )

        viewBinding.largePlayer.titleBackground.setBackgroundColor(bodyColor)
        viewBinding.largePlayer.largeTitle.setTextColor(titleTextColor)
        viewBinding.largePlayer.largeSubTitle.setTextColor(bodyTextColor)
    }
}