package com.joe.podcastplayer.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.extension.*
import com.joe.podcastplayer.service.media.PodcastServiceConnection
import com.prof.rssparser.FeedItem

class EpisodeViewModel(private val podcastServiceConnection: PodcastServiceConnection) : ViewModel() {

    fun playMedia(feedItem: FeedItem?) {
        if (feedItem == null) return
        val nowPlaying = podcastServiceConnection.nowPlaying.value
        val transportControls = podcastServiceConnection.transportControls

        val isPrepared = podcastServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && feedItem.guid.toString() == nowPlaying?.id.toString()) {
            podcastServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(
                            className, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaId=${feedItem.audio.toString()})"
                        )
                    }
                }
            }
        } else {
            transportControls.playFromUri(Uri.parse(feedItem.audio), null)
        }
    }

    override fun onCleared() {}

    class Factory(private val podcastServiceConnection: PodcastServiceConnection) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EpisodeViewModel(podcastServiceConnection) as T
        }
    }
}