package com.joe.podcastplayer.service.ui.song

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.service.data.Song
import com.joe.podcastplayer.service.extension.*
import com.joe.podcastplayer.service.media.MusicServiceConnection
import com.prof.rssparser.FeedItem

private const val TAG = "SongListViewModel"

class EpisodeViewModel(private val musicServiceConnection: MusicServiceConnection) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = _songs

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

    fun playMediaId(mediaLink: String) {
        val nowPlaying = musicServiceConnection.nowPlaying.value
        val transportControls = musicServiceConnection.transportControls

        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaLink == nowPlaying?.mediaUri.toString()) {
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(
                            TAG, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaId=$mediaLink)"
                        )
                    }
                }
            }
        } else {
            transportControls.playFromUri(Uri.parse(mediaLink), null)
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