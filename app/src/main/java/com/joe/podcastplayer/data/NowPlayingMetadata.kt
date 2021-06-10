package com.joe.podcastplayer.data

import android.content.Context
import android.net.Uri
import com.joe.podcastplayer.R
import kotlin.math.floor

/**
 * Utility class used to represent the metadata necessary to display the
 * media item currently being played.
 */
data class NowPlayingMetadata(
    val id: String,
    val albumArtUri: Uri,
    val mediaUri: Uri,
    val title: String?,
    val subtitle: String?,
    val duration: String
) {
    companion object {
        /**
         * Utility method to convert milliseconds to a display of minutes and seconds
         */
        fun timestampToMSS(context: Context, position: Long): String {
            val totalSeconds = floor(position / 1E3).toInt()
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds - (minutes * 60)
            return if (position < 0) context.getString(R.string.duration_unknown)
            else context.getString(R.string.duration_format).format(minutes, remainingSeconds)
        }
    }
}