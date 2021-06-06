package com.joe.podcastplayer.service.extension

import android.support.v4.media.MediaMetadataCompat
import com.joe.podcastplayer.service.data.Song
import com.prof.rssparser.FeedItem

fun FeedItem.toMediaMetadataCompat(): MediaMetadataCompat =
    MediaMetadataCompat.Builder().also {
        it.id = guid.toString()
        it.title = title
        it.artist = author
        it.albumArtUri = image
        it.mediaUri = audio.toString()

        it.displayTitle = title
        it.displaySubtitle = author
        it.displayDescription = description
        it.displayIconUri = image
    }.build()

@JvmName("toMediaMetadataCompatFeedItem")
fun List<FeedItem>.toMediaMetadataCompat(): List<MediaMetadataCompat> =
    this.map { it.toMediaMetadataCompat() }

fun Song.toMediaMetadataCompat(): MediaMetadataCompat =
    MediaMetadataCompat.Builder().also {
        it.id = id.toString()
        it.title = title
        it.artist = artistName
        it.albumArtUri = coverPath
        it.mediaUri = contentUri.toString()

        it.displayTitle = title
        it.displaySubtitle = artistName
        it.displayDescription = albumName
        it.displayIconUri = coverPath
    }.build()

fun List<Song>.toMediaMetadataCompat(): List<MediaMetadataCompat> =
    this.map { it.toMediaMetadataCompat() }