package com.joe.podcastplayer.playback

import android.content.Context
import android.view.SurfaceHolder
import java.io.IOException

interface IPlayer {
    fun initMediaPlayer(context: Context)
    fun canDownmix(): Boolean
    val currentSpeedMultiplier: Float

    fun pause()
    fun start()
    fun stop()

    @Throws(IllegalStateException::class, IOException::class)
    fun prepare()
    fun release()
    fun reset()
    fun seekTo(msec: Int)
    fun setAudioStreamType(streamtype: Int)
    fun getTotalDuration(): Int
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean


    @Throws(IllegalStateException::class, IOException::class, IllegalArgumentException::class, SecurityException::class)
    fun setDataSource(path: String?)

    @Throws(IOException::class)
    fun setDataSource(streamUrl: String?, username: String?, password: String?)
    fun setDisplay(sh: SurfaceHolder?)
    fun setPlaybackParams(speed: Float, skipSilence: Boolean)
    fun setAudioTrack(track: Int)
    fun setVolume(left: Float, right: Float)
    val audioTracks: List<String?>?
    val selectedAudioTrack: Int
}