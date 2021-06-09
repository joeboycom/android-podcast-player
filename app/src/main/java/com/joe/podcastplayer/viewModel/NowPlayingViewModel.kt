package com.joe.podcastplayer.viewModel

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.R
import com.joe.podcastplayer.extension.className
import com.joe.podcastplayer.extension.*
import com.joe.podcastplayer.service.media.EMPTY_PLAYBACK_STATE
import com.joe.podcastplayer.service.media.PodcastServiceConnection
import com.joe.podcastplayer.service.media.NOTHING_PLAYING
import kotlin.math.floor

class NowPlayingViewModel(
    private val context: Context,
    podcastServiceConnection: PodcastServiceConnection
) : ViewModel() {

    /**
     * Utility class used to represent the metadata necessary to display the
     * media item currently being played.
     */
    data class NowPlayingMetadata(
        val id: String,
        val albumArtUri: Uri,
        val mediaUri: Uri,
        val title: String?,
        val subtitle: String?,
        val duration: String
    ) {
        companion object {
            /**
             * Utility method to convert milliseconds to a display of minutes and seconds
             */
            fun timestampToMSS(context: Context, position: Long): String {
                val totalSeconds = floor(position / 1E3).toInt()
                val minutes = totalSeconds / 60
                val remainingSeconds = totalSeconds - (minutes * 60)
                return if (position < 0) context.getString(R.string.duration_unknown)
                else context.getString(R.string.duration_format).format(minutes, remainingSeconds)
            }
        }
    }

    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
    val mediaMetadata = MutableLiveData<NowPlayingMetadata>()
    val mediaPosition = MutableLiveData<Long>().apply {
        postValue(0L)
    }

    val mediaPlayProgress = MutableLiveData<Int>().apply {
        postValue(0)
    }

    val mediaButtonRes = MutableLiveData<IntArray>()

    private var updatePosition = true
    private var mediaDuration = 0L
    private val handler = Handler(Looper.getMainLooper())

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = podcastServiceConnection.nowPlaying.value ?: NOTHING_PLAYING
        updateState(playbackState, metadata)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        updateState(playbackState, it)
        mediaDuration = it.duration
    }

    private val playerServiceConnection = podcastServiceConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        checkPlaybackPosition()
    }

    fun skipToNext() {
        playerServiceConnection.transportControls.skipToNext()
    }

    fun skipToPrevious() {
        playerServiceConnection.transportControls.skipToPrevious()
    }

    fun rewind() {
        playerServiceConnection.transportControls.rewind()
    }

    fun fastForward() {
        playerServiceConnection.transportControls.fastForward()
    }

    fun changePlaybackPosition(seekBarProgress: Int, seekBarMax: Int) {
        Log.e(className, "seekBarProgress:$seekBarProgress seekBarMax:$seekBarMax")
        val lastPosition = mediaDuration * 1L * seekBarProgress / seekBarMax
        Log.e(className, "lastPosition:$lastPosition")
        playerServiceConnection.transportControls.seekTo(lastPosition)
    }

    /**
     * Internal function that recursively calls itself every [POSITION_UPDATE_INTERVAL_MILLIS] ms
     * to check the current playback position and updates the corresponding LiveData object when it
     * has changed.
     */
    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = playbackState.currentPlayBackPosition
        if (mediaPosition.value != currPosition) {
            mediaPosition.postValue(currPosition)
            if (mediaDuration > 0) {
                val progress = ((currPosition * 100 / mediaDuration)).toInt()
                mediaPlayProgress.postValue(progress)
            }
        }

        if (updatePosition)
            checkPlaybackPosition()
    }, POSITION_UPDATE_INTERVAL_MILLIS)


    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ) {
        // Only update media item once we have duration available
        if (mediaMetadata.duration != 0L && mediaMetadata.id != null) {
            val nowPlayingMetadata = NowPlayingMetadata(
                mediaMetadata.id!!,
                mediaMetadata.displayIconUri,
                mediaMetadata.mediaUri,
                mediaMetadata.title?.trim(),
                mediaMetadata.displaySubtitle?.trim(),
                NowPlayingMetadata.timestampToMSS(context, mediaMetadata.duration)
            )
            this.mediaMetadata.postValue(nowPlayingMetadata)
        }

        mediaButtonRes.postValue(
            when (playbackState.isPlaying) {
                true -> intArrayOf(-R.attr.state_play, R.attr.state_pause) //Set pause
                else -> intArrayOf(R.attr.state_play, -R.attr.state_pause) //Set play
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        // Remove the permanent observers from the PodcastServiceConnection.
        playerServiceConnection.playbackState.removeObserver(playbackStateObserver)
        playerServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)

        // Stop updating the position
        updatePosition = false
    }

    class Factory(
        private val context: Context,
        private val podcastServiceConnection: PodcastServiceConnection
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NowPlayingViewModel(context, podcastServiceConnection) as T
        }
    }
}

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L

