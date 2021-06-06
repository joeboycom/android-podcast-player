package com.joe.podcastplayer.service.di

import android.content.ComponentName
import android.content.Context
import com.google.gson.reflect.TypeToken
import com.joe.podcastplayer.extension.gson
import com.joe.podcastplayer.service.media.MusicService
import com.joe.podcastplayer.service.media.MusicServiceConnection
import com.joe.podcastplayer.service.ui.nowplaying.NowPlayingViewModel
import com.joe.podcastplayer.service.ui.song.EpisodeViewModel
import com.prof.rssparser.FeedItem

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

    fun provideSongListRepository(context: Context): ArrayList<FeedItem> {
        val pref = context.getSharedPreferences("podcast_player", Context.MODE_PRIVATE)
        return gson.fromJson(pref.getString("pref_feed_item_list", ""), object : TypeToken<ArrayList<FeedItem>>() {}.type)
    }

    fun provideSongListViewModel(context: Context): EpisodeViewModel.Factory {
        val musicServiceConnection = provideMusicServiceConnection(context)
        return EpisodeViewModel.Factory(musicServiceConnection)
    }

    fun provideNowPlayingViewModel(context: Context): NowPlayingViewModel.Factory {
        val musicServiceConnection = provideMusicServiceConnection(context)
        return NowPlayingViewModel.Factory(
            context,
            musicServiceConnection
        )
    }

}