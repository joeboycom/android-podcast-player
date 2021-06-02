package com.joe.podcastplayer.playback;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.prof.rssparser.FeedItem;

import java.util.Date;

public class FeedMedia implements Playable {
    public static final int FEEDFILETYPE_FEEDMEDIA = 2;
    public static final int PLAYABLE_TYPE_FEEDMEDIA = 1;
    public static final String FILENAME_PREFIX_EMBEDDED_COVER = "metadata-retriever:";

    public static final String PREF_MEDIA_ID = "FeedMedia.PrefMediaId";
    private static final String PREF_FEED_ID = "FeedMedia.PrefFeedId";

    /**
     * Indicates we've checked on the size of the item via the network
     * and got an invalid response. Using Integer.MIN_VALUE because
     * 1) we'll still check on it in case it gets downloaded (it's <= 0)
     * 2) By default all FeedMedia have a size of 0 if we don't know it,
     *    so this won't conflict with existing practice.
     */
    private static final int CHECKED_ON_SIZE_BUT_UNKNOWN = Integer.MIN_VALUE;

    private int duration;
    private int position; // Current position in file
    private long lastPlayedTime; // Last time this media was played (in ms)
    private int played_duration; // How many ms of this file have been played
    private long size; // File size in Byte
    private String mime_type;
    @Nullable private volatile FeedItem item;
    private Date playbackCompletionDate;
    private int startPosition = -1;
    private int playedDurationWhenStarted;

    // if null: unknown, will be checked
    private Boolean hasEmbeddedPicture;

    /* Used for loading item when restoring from parcel. */
    private long itemID;

    public FeedMedia(FeedItem i, String download_url, long size,
                     String mime_type) {
        this.item = i;
        this.size = size;
        this.mime_type = mime_type;
    }

    public FeedMedia(long id, FeedItem item, int duration, int position,
                     long size, String mime_type, String file_url, String download_url,
                     boolean downloaded, Date playbackCompletionDate, int played_duration,
                     long lastPlayedTime) {
        this.item = item;
        this.duration = duration;
        this.position = position;
        this.played_duration = played_duration;
        this.playedDurationWhenStarted = played_duration;
        this.size = size;
        this.mime_type = mime_type;
        this.playbackCompletionDate = playbackCompletionDate == null
                ? null : (Date) playbackCompletionDate.clone();
        this.lastPlayedTime = lastPlayedTime;
    }

    public FeedMedia(long id, FeedItem item, int duration, int position,
                     long size, String mime_type, String file_url, String download_url,
                     boolean downloaded, Date playbackCompletionDate, int played_duration,
                     Boolean hasEmbeddedPicture, long lastPlayedTime) {
        this(id, item, duration, position, size, mime_type, file_url, download_url, downloaded,
                playbackCompletionDate, played_duration, lastPlayedTime);
        this.hasEmbeddedPicture = hasEmbeddedPicture;
    }
//
//    /**
//     * Returns a MediaItem representing the FeedMedia object.
//     * This is used by the MediaBrowserService
//     */
//    public MediaBrowserCompat.MediaItem getMediaItem() {
//        Playable p = this;
//        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder()
//                .setMediaId(String.valueOf(id))
//                .setTitle(p.getEpisodeTitle())
//                .setDescription(p.getFeedTitle())
//                .setSubtitle(p.getFeedTitle());
//        if (item != null) {
//            // getImageLocation() also loads embedded images, which we can not send to external devices
//            if (item.getImageUrl() != null) {
//                builder.setIconUri(Uri.parse(item.getImageUrl()));
//            } else if (item.getFeed() != null && item.getFeed().getImageUrl() != null) {
//                builder.setIconUri(Uri.parse(item.getFeed().getImageUrl()));
//            }
//        }
//        return new MediaBrowserCompat.MediaItem(builder.build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
//    }

    /**
     * Uses mimetype to determine the type of media.
     */
    public MediaType getMediaType() {
        return MediaType.fromMimeType(mime_type);
    }

    @Override
    public String getStreamUrl() {
        return null;
    }

    @Override
    public String getEpisodeTitle() {
        return null;
    }

    @Override
    public String getFeedTitle() {
        return null;
    }

    @Override
    public Date getPubDate() {
        return null;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPlayedDuration() {
        return played_duration;
    }

    public int getPlayedDurationWhenStarted() {
        return playedDurationWhenStarted;
    }

    public void setPlayedDuration(int played_duration) {
        this.played_duration = played_duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
//        if(position > 0 && item != null && item.isNew()) {
//            this.item.setPlayed(false);
//        }
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String getDescription() {
        if (item != null) {
            return item.getDescription();
        }
        return null;
    }

    /**
     * Indicates we asked the service what the size was, but didn't
     * get a valid answer and we shoudln't check using the network again.
     */
    public void setCheckedOnSizeButUnknown() {
        this.size = CHECKED_ON_SIZE_BUT_UNKNOWN;
    }

    public boolean checkedOnSizeButUnknown() {
        return (CHECKED_ON_SIZE_BUT_UNKNOWN == this.size);
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    @Nullable
    public FeedItem getItem() {
        return item;
    }

    public Date getPlaybackCompletionDate() {
        return playbackCompletionDate == null
                ? null : (Date) playbackCompletionDate.clone();
    }

    public void setPlaybackCompletionDate(Date playbackCompletionDate) {
        this.playbackCompletionDate = playbackCompletionDate == null
                ? null : (Date) playbackCompletionDate.clone();
    }

    public boolean isInProgress() {
        return (this.position > 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public long getItemId() {
        return itemID;
    }

    @Override
    public void onPlaybackStart() {
        startPosition = Math.max(position, 0);
        playedDurationWhenStarted = played_duration;
    }

    @Override
    public void onPlaybackPause(Context context) {
        if (position > startPosition) {
            played_duration = playedDurationWhenStarted + position - startPosition;
            playedDurationWhenStarted = played_duration;
        }
        startPosition = position;
    }

    @Override
    public void onPlaybackCompleted(Context context) {
        startPosition = -1;
    }

    @Override
    public int getPlayableType() {
        return PLAYABLE_TYPE_FEEDMEDIA;
    }

    public static final Parcelable.Creator<FeedMedia> CREATOR = new Parcelable.Creator<FeedMedia>() {
        public FeedMedia createFromParcel(Parcel in) {
            final long id = in.readLong();
            final long itemID = in.readLong();
            FeedMedia result = new FeedMedia(id, null, in.readInt(), in.readInt(), in.readLong(), in.readString(), in.readString(),
                    in.readString(), in.readByte() != 0, new Date(in.readLong()), in.readInt(), in.readLong());
            result.itemID = itemID;
            return result;
        }

        public FeedMedia[] newArray(int size) {
            return new FeedMedia[size];
        }
    };
}
