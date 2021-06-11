package com.joe.podcastplayer.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.data.NowPlayingMetadata
import com.joe.podcastplayer.extension.*
import com.joe.podcastplayer.service.media.PodcastServiceConnection

class MainActivityViewModel(private val podcastServiceConnection: PodcastServiceConnection) : ViewModel() {

    val preparePlayingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun playMedia(playingMetadata: NowPlayingMetadata) {
        val nowPlaying = podcastServiceConnection.nowPlaying.value
        val transportControls = podcastServiceConnection.transportControls

        val isPrepared = podcastServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && playingMetadata.id == nowPlaying?.id.toString()) {
            podcastServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(
                            className, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaUri=${nowPlaying!!.mediaUri})"
                        )
                    }
                }
            }
        } else {
            transportControls.playFromUri(playingMetadata.mediaUri, null)
            preparePlayingLiveData.postValue(true)
        }
    }

    override fun onCleared() {}

    class Factory(private val podcastServiceConnection: PodcastServiceConnection) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(podcastServiceConnection) as T
        }
    }
}