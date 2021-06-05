package com.joe.podcastplayer.service.di

import android.content.ComponentName
import android.content.Context
import com.joe.podcastplayer.service.media.MusicService
import com.joe.podcastplayer.service.media.MusicServiceConnection
import com.joe.podcastplayer.service.repository.SongListRepositoryImpl
import com.joe.podcastplayer.service.ui.nowplaying.NowPlayingViewModel
import com.joe.podcastplayer.service.ui.song.SongListViewModel

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun provideMusicServiceConnection(context: Context): MusicServiceConnection {
        return MusicServiceConnection.getInstance(
            context,
            ComponentName(context, MusicService::class.java)
        )
    }

    fun provideSongListRepository(context: Context) =
        SongListRepositoryImpl(context.contentResolver)

    fun provideSongListViewModel(context: Context): SongListViewModel.Factory {
        val contentResolver = context.contentResolver
        val musicServiceConnection = provideMusicServiceConnection(context)
        return SongListViewModel.Factory(
            contentResolver,
            provideSongListRepository(context),
            musicServiceConnection
        )
    }

    fun provideNowPlayingViewModel(context: Context): NowPlayingViewModel.Factory {
        val musicServiceConnection = provideMusicServiceConnection(context)
        return NowPlayingViewModel.Factory(
            context,
            musicServiceConnection
        )
    }

}