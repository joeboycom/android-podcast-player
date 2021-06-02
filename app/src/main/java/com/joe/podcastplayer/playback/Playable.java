package com.joe.podcastplayer.playback;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.Date;


/**
 * Interface for objects that can be played by the PlaybackService.
 */
public interface Playable extends Parcelable {
    int INVALID_TIME = -1;

    /**
     * Returns the title of the episode that this playable represents
     */
    String getEpisodeTitle();

    /**
     * Returns the title of the feed this Playable belongs to.
     */
    String getFeedTitle();

    /**
     * Returns the published date
     */
    Date getPubDate();

    /**
     * Return duration of object or 0 if duration is unknown.
     */
    int getDuration();

    /**
     * Return position of object or 0 if position is unknown.
     */
    int getPosition();

    /**
     * Returns the description of the item, if available.
     * For FeedItems, the description needs to be loaded from the database first.
     */
    @Nullable
    String getDescription();

    /**
     * Returns the type of media.
     */
    MediaType getMediaType();

    /**
     * Returns an url to a file that can be streamed by the player or null if
     * this url is not known.
     */
    String getStreamUrl();

    void setPosition(int newPosition);

    void setDuration(int newDuration);

    /**
     * This method should be called every time playback starts on this object.
     * <p/>
     * Position held by this Playable should be set accurately before a call to this method is made.
     */
    void onPlaybackStart();

    /**
     * This method should be called every time playback pauses or stops on this object,
     * including just before a seeking operation is performed, after which a call to
     * {@link #onPlaybackStart()} should be made. If playback completes, calling this method is not
     * necessary, as long as a call to {@link #onPlaybackCompleted(Context)} is made.
     * <p/>
     * Position held by this Playable should be set accurately before a call to this method is made.
     */
    void onPlaybackPause(Context context);

    /**
     * This method should be called when playback completes for this object.
     * @param context
     */
    void onPlaybackCompleted(Context context);

    /**
     * Returns an integer that must be unique among all Playable classes. The
     * return value is later used by PlayableUtils to determine the type of the
     * Playable object that is restored.
     */
    int getPlayableType();


}
