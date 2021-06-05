package com.joe.podcastplayer.service.repository

import com.joe.podcastplayer.service.data.Song

interface SongListRepository {

    suspend fun getSongs(): List<Song>
}