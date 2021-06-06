package com.joe.podcastplayer.service.ui.song

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.joe.podcastplayer.service.data.Song
import com.joe.podcastplayer.service.extension.*
import com.joe.podcastplayer.service.media.MusicServiceConnection
import com.joe.podcastplayer.service.repository.SongListRepository
import com.prof.rssparser.FeedItem
import kotlinx.coroutines.launch

private const val TAG = "SongListViewModel"

class EpisodeViewModel(
    private val contentResolver: ContentResolver,
    private val songListRepository: SongListRepository,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private var contentObserver: ContentObserver? = null
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = _songs

    fun loadSongs() {
        viewModelScope.launch {
            val songList = querySongs()
            _songs.postValue(songList)
            if (contentObserver == null) {
                contentObserver = contentResolver.registerObserver(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                ) {
                    loadSongs()
                }
            }
        }
    }

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

    private suspend fun querySongs(): List<Song> =
        songListRepository.getSongs()

    override fun onCleared() {
        contentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }

    class Factory(
        private val contentResolver: ContentResolver,
        private val songListRepository: SongListRepository,
        private val musicServiceConnection: MusicServiceConnection
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EpisodeViewModel(
                contentResolver,
                songListRepository,
                musicServiceConnection
            ) as T
        }
    }
}