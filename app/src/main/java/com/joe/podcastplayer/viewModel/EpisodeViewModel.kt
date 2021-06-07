package com.joe.podcastplayer.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.extension.*
import com.joe.podcastplayer.service.media.MusicServiceConnection
import com.prof.rssparser.FeedItem

private const val TAG = "EpisodeViewModel"

class EpisodeViewModel(private val musicServiceConnection: MusicServiceConnection) : ViewModel() {

    fun playMedia(feedItem: FeedItem?, pauseAllowed: Boolean = true) {
        Log.e("HAHA", "playMedia:$feedItem")
        if (feedItem == null) return
        val nowPlaying = musicServiceConnection.nowPlaying.value
        val transportControls = musicServiceConnection.transportControls

        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        Log.e("HAHA", "playbackState1:${feedItem.guid.toString()}")
        Log.e("HAHA", "playbackState2:${nowPlaying?.id.toString()}")
        if (isPrepared && feedItem.guid.toString() == nowPlaying?.id.toString()) {
            musicServiceConnection.playbackState.value?.let { playbackState ->
                Log.e("HAHA", "playbackState:$playbackState")
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(
                            TAG, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaId=${feedItem.audio.toString()})"
                        )
                    }
                }
            }
        } else {
            Log.e("HAHA", "playMedia playFromUri $feedItem.audio")
            transportControls.playFromUri(Uri.parse(feedItem.audio), null)
        }
    }

    override fun onCleared() {}

    class Factory(private val musicServiceConnection: MusicServiceConnection) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EpisodeViewModel(musicServiceConnection) as T
        }
    }
}