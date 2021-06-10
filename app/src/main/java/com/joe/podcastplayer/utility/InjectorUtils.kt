package com.joe.podcastplayer.utility

import android.content.ComponentName
import android.content.Context
import com.google.gson.reflect.TypeToken
import com.joe.podcastplayer.extension.gson
import com.joe.podcastplayer.service.media.PodcastService
import com.joe.podcastplayer.service.media.PodcastServiceConnection
import com.joe.podcastplayer.viewModel.EpisodeViewModel
import com.joe.podcastplayer.viewModel.NowPlayingViewModel
import com.prof.rssparser.FeedItem

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun provideMediaServiceConnection(context: Context): PodcastServiceConnection {
        return PodcastServiceConnection.getInstance(context, ComponentName(context, PodcastService::class.java))
    }

    fun provideFeedItemListRepository(context: Context): ArrayList<FeedItem> {
        val pref = context.getSharedPreferences("podcast_player", Context.MODE_PRIVATE)
        return gson.fromJson(pref.getString("pref_feed_item_list", ""), object : TypeToken<ArrayList<FeedItem>>() {}.type)
    }

    fun provideFeedItemListViewModel(context: Context): EpisodeViewModel.Factory {
        val musicServiceConnection = provideMediaServiceConnection(context)
        return EpisodeViewModel.Factory(musicServiceConnection)
    }

    fun provideNowPlayingViewModel(context: Context): NowPlayingViewModel.Factory {
        val musicServiceConnection = provideMediaServiceConnection(context)
        return NowPlayingViewModel.Factory(context, musicServiceConnection)
    }

}