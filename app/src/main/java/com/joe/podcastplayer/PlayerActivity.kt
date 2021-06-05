package com.joe.podcastplayer

import android.content.ComponentName
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.joe.podcastplayer.PlayerActivity
import com.joe.podcastplayer.playback.BackgroundAudioService

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_PAUSED = 0
        private const val STATE_PLAYING = 1
    }

    private var mCurrentState = 0
    private var mMediaBrowserCompat: MediaBrowser? = null
    private var mMediaControllerCompat: MediaController? = null
    private var mPlayPauseToggleButton: Button? = null
    private val mMediaBrowserCompatConnectionCallback: MediaBrowser.ConnectionCallback = object : MediaBrowser.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            try {
                mMediaControllerCompat = MediaController(this@PlayerActivity, mMediaBrowserCompat!!.sessionToken)
                mMediaControllerCompat!!.registerCallback(mMediaControllerCompatCallback)
                mediaController = mMediaControllerCompat
                mediaController.transportControls.playFromMediaId(R.raw.warner_tautz_off_broadway.toString(), null)
            } catch (e: Exception) {
            }
        }
    }
    private val mMediaControllerCompatCallback: MediaController.Callback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            if (state == null) {
                return
            }
            when (state.state) {
                PlaybackState.STATE_PLAYING -> {
                    mCurrentState = STATE_PLAYING
                }
                PlaybackState.STATE_PAUSED -> {
                    mCurrentState = STATE_PAUSED
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mPlayPauseToggleButton = findViewById<View>(R.id.button) as Button
        mMediaBrowserCompat = MediaBrowser(
            this, ComponentName(this, BackgroundAudioService::class.java),
            mMediaBrowserCompatConnectionCallback, intent.extras
        )
        mMediaBrowserCompat!!.connect()
        mPlayPauseToggleButton!!.setOnClickListener {
            mCurrentState = if (mCurrentState == STATE_PAUSED) {
                mediaController.transportControls.play()
                STATE_PLAYING
            } else {
                if (mediaController.playbackState!!.state == PlaybackState.STATE_PLAYING) {
                    mediaController.transportControls.pause()
                }
                STATE_PAUSED
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaController.playbackState!!.state == PlaybackState.STATE_PLAYING) {
            mediaController.transportControls.pause()
        }
        mMediaBrowserCompat!!.disconnect()
    }
}