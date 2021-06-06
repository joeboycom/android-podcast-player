package com.joe.podcastplayer

import android.content.ComponentName
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.joe.podcastplayer.playback.BackgroundAudioService
import com.joe.podcastplayer.service.di.InjectorUtils

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_PAUSED = 0
        private const val STATE_PLAYING = 1
    }

    private val playerViewModel: PlayerViewModel by viewModels {
        InjectorUtils.provideNowPlayingViewModel(this)
    }

    private var mCurrentState = 0
    private var mMediaBrowserCompat: MediaBrowser? = null
    private var mMediaControllerCompat: MediaController? = null
    private var mPlayPauseToggleButton: Button? = null
    private val mMediaBrowserCompatItemCallback: MediaBrowser.ItemCallback = @RequiresApi(Build.VERSION_CODES.M)
    object : MediaBrowser.ItemCallback() {
        override fun onItemLoaded(item: MediaBrowser.MediaItem) {
            super.onItemLoaded(item)
            Log.e("HAHA", "onItemLoaded: $item")
        }
    }
    private val mMediaBrowserCompatConnectionCallback: MediaBrowser.ConnectionCallback = object : MediaBrowser.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            try {
                mMediaControllerCompat = MediaController(this@PlayerActivity, mMediaBrowserCompat!!.sessionToken)
                mMediaControllerCompat!!.registerCallback(mMediaControllerCompatCallback)
                mediaController = mMediaControllerCompat
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.e("HAHA", "onConnected")
                    mediaController.transportControls.playFromUri(Uri.parse("https://feeds.soundcloud.com/stream/1036317475-daodutech-podcast-colorful-desktop-computer.mp3"), null)
//                    mediaController.transportControls.playFromMediaId("https://feeds.soundcloud.com/stream/1036317475-daodutech-podcast-colorful-desktop-computer.mp3", null)
//                    mediaController.transportControls.playFromMediaId(R.raw.warner_tautz_off_broadway.toString(), null)

                }
            } catch (e: Exception) {
            }
        }
    }
    private val mMediaControllerCompatCallback: MediaController.Callback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            Log.e("HAHA", "onPlaybackStateChanged 1 :${state.toString()}")
            Log.e("HAHA", "onPlaybackStateChanged 2 :${state?.position}")
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

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            Log.e("HAHA", "MediaControllerCallback onMetadataChanged:${metadata.toString()}")
            val duration  = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
            Log.e("HAHA", "MediaControllerCallback onMetadataChanged duration:${duration}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mPlayPauseToggleButton = findViewById<View>(R.id.button) as Button
        val getInfoButton = findViewById<View>(R.id.getInfoButton) as Button
        mMediaBrowserCompat = MediaBrowser(
            this, ComponentName(this, BackgroundAudioService::class.java),
            mMediaBrowserCompatConnectionCallback, intent.extras
        )
        mMediaBrowserCompat!!.connect()
        mPlayPauseToggleButton!!.setOnClickListener {
            mCurrentState = if (mCurrentState == STATE_PAUSED) {
                Log.e("HAHA", "1")
                mediaController.transportControls.play()
                STATE_PLAYING
            } else {
                Log.e("HAHA", "2")
                if (mediaController.playbackState!!.state == PlaybackState.STATE_PLAYING) {
                    mediaController.transportControls.pause()
                }
                STATE_PAUSED
            }
        }

        getInfoButton.setOnClickListener {
            Log.e("HAHA", "mMediaControllerCompat?.metadata.toString() ${mMediaControllerCompat?.metadata.toString()}")
            mediaController.transportControls.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaController.playbackState!!.state == PlaybackState.STATE_PLAYING) {
            mediaController.transportControls.pause()
        }
        mMediaBrowserCompat!!.disconnect()

//        mediaController.transportControls.seekTo()
    }
}