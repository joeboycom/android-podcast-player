package com.joe.podcastplayer.playback

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.PowerManager
import android.view.SurfaceHolder


class AudioPlayer : IPlayer {

    companion object {
        private const val TAG = "AudioPlayer"
    }

    private var mediaPlayer: MediaPlayer? = null
    var onMpPrepareListener: ((mp: MediaPlayer) -> Unit)? = null
    var onMpBufferingUpdateListener: ((currentPosition: Int) -> Unit)? = null

    override fun setPlaybackParams(speed: Float, skipSilence: Boolean) {
        //Default player does not support silence skipping
    }

    override val audioTracks: List<String?>?
        get() = emptyList()

    override fun setAudioTrack(track: Int) {}
    override fun setVolume(left: Float, right: Float) {
        mediaPlayer?.setVolume(left, right)
    }

    override val selectedAudioTrack: Int
        get() = -1


    override fun initMediaPlayer(context: Context) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.setVolume(1.0f, 1.0f)
        mediaPlayer?.setOnInfoListener { mp, what, extra ->
            false
//            val metadataBuilder = MediaMetadata.Builder()
//            metadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, mp.duration)
        }
        mediaPlayer?.setOnPreparedListener {
            start()
            onMpPrepareListener?.invoke(it)
        }

        mediaPlayer?.setOnBufferingUpdateListener { mp, percent ->
            onMpBufferingUpdateListener?.invoke(mp.currentPosition)
        }

        mediaPlayer?.setOnCompletionListener {
        }
    }

    override fun canDownmix(): Boolean {
        return false
    }

    override val currentSpeedMultiplier: Float
        get() = 0F

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun prepare() {
        mediaPlayer?.prepare()
    }
    override fun release() {
        mediaPlayer?.release()
    }

    override fun reset() {
        mediaPlayer?.reset()
    }

    override fun seekTo(msec: Int) {
        mediaPlayer?.seekTo(msec)
    }

    override fun setAudioStreamType(streamtype: Int) {}

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun setDataSource(path: String?) {
        mediaPlayer?.setDataSource(path)
    }

    override fun getTotalDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    override fun setDataSource(streamUrl: String?, username: String?, password: String?) {
        setDataSource(streamUrl)
    }

    override fun setDisplay(sh: SurfaceHolder?) {}
}