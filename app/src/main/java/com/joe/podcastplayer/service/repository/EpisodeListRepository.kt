package com.joe.podcastplayer.service.repository

import com.prof.rssparser.FeedItem

interface EpisodeListRepository {

    suspend fun getSongs(): List<FeedItem>
}