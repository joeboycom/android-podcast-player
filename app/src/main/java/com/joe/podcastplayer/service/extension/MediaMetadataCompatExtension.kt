package com.joe.podcastplayer.service.extension

import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource

/**
 * Helper extension to convert a potentially null [String] to a [Uri] falling back to [Uri.EMPTY]
 */
fun String?.toUri(): Uri = this?.let { Uri.parse(it) } ?: Uri.EMPTY

/**
 * Useful extensions for [MediaMetadataCompat].
 */
inline val MediaMetadataCompat.id: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.title: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

inline val MediaMetadataCompat.artist: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

inline val MediaMetadataCompat.duration
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

inline val MediaMetadataCompat.album: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

inline val MediaMetadataCompat.author: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_AUTHOR)

inline val MediaMetadataCompat.writer: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_WRITER)

inline val MediaMetadataCompat.composer: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_COMPOSER)

inline val MediaMetadataCompat.compilation: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_COMPILATION)

inline val MediaMetadataCompat.date: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DATE)

inline val MediaMetadataCompat.year: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_YEAR)

inline val MediaMetadataCompat.genre: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_GENRE)

inline val MediaMetadataCompat.trackNumber
    get() = getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)

inline val MediaMetadataCompat.trackCount
    get() = getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)

inline val MediaMetadataCompat.discNumber
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER)

inline val MediaMetadataCompat.albumArtist: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)

inline val MediaMetadataCompat.art: Bitmap
    get() = getBitmap(MediaMetadataCompat.METADATA_KEY_ART)

inline val MediaMetadataCompat.artUri: Uri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_ART_URI).toUri()

inline val MediaMetadataCompat.albumArt: Bitmap?
    get() = getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)

inline val MediaMetadataCompat.albumArtUri: Uri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI).toUri()

inline val MediaMetadataCompat.userRating
    get() = getLong(MediaMetadataCompat.METADATA_KEY_USER_RATING)

inline val MediaMetadataCompat.rating
    get() = getLong(MediaMetadataCompat.METADATA_KEY_RATING)

inline val MediaMetadataCompat.displayTitle: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)

inline val MediaMetadataCompat.displaySubtitle: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE)

inline val MediaMetadataCompat.displayDescription: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)

inline val MediaMetadataCompat.displayIcon: Bitmap
    get() = getBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON)

inline val MediaMetadataCompat.displayIconUri: Uri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI).toUri()

inline val MediaMetadataCompat.mediaUri: Uri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri()

inline val MediaMetadataCompat.downloadStatus
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS)


const val NO_GET = "Property does not have a 'get'"

inline var MediaMetadataCompat.Builder.id: String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, value)
    }

inline var MediaMetadataCompat.Builder.title: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, value)
    }

inline var MediaMetadataCompat.Builder.artist: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ARTIST, value)
    }

inline var MediaMetadataCompat.Builder.album: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM, value)
    }

inline var MediaMetadataCompat.Builder.duration: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_DURATION, value)
    }

inline var MediaMetadataCompat.Builder.genre: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_GENRE, value)
    }

inline var MediaMetadataCompat.Builder.mediaUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, value)
    }

inline var MediaMetadataCompat.Builder.albumArtUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, value)
    }

inline var MediaMetadataCompat.Builder.albumArt: Bitmap?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, value)
    }

inline var MediaMetadataCompat.Builder.trackNumber: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, value)
    }

inline var MediaMetadataCompat.Builder.trackCount: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, value)
    }

inline var MediaMetadataCompat.Builder.displayTitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, value)
    }

inline var MediaMetadataCompat.Builder.displaySubtitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, value)
    }

inline var MediaMetadataCompat.Builder.displayDescription: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, value)
    }

inline var MediaMetadataCompat.Builder.displayIconUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, value)
    }

inline var MediaMetadataCompat.Builder.downloadStatus: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS, value)
    }


/**
 * Extension method for building a [ConcatenatingMediaSource] given a [List]
 * of [MediaMetadataCompat] objects.
 */
fun List<MediaMetadataCompat>.toMediaSource(
    dataSourceFactory: DataSource.Factory
): ConcatenatingMediaSource {

    val concatenatingMediaSource = ConcatenatingMediaSource()
    forEach {
        concatenatingMediaSource.addMediaSource(it.toMediaSource(dataSourceFactory))
    }
    return concatenatingMediaSource
}

/**
 * Extension method for building an [ExtractorMediaSource] from a [MediaMetadataCompat] object.
 *
 * For convenience, place the [MediaDescriptionCompat] into the tag so it can be retrieved later.
 */
private fun MediaMetadataCompat.toMediaSource(dataSourceFactory: DataSource.Factory) =
    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaUri)