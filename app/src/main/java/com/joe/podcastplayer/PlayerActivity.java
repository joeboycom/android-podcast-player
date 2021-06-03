package com.joe.podcastplayer;

import android.content.ComponentName;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.joe.podcastplayer.playback.BackgroundAudioService;

public class PlayerActivity extends AppCompatActivity {

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

    private MediaBrowser mMediaBrowserCompat;
    private MediaController mMediaControllerCompat;

    private Button mPlayPauseToggleButton;

    private MediaBrowser.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowser.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaController(PlayerActivity.this, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                setMediaController(mMediaControllerCompat);
                getMediaController().getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);

            } catch(Exception e) {

            }
        }
    };

    private MediaController.Callback mMediaControllerCompatCallback = new MediaController.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            super.onPlaybackStateChanged(state);
            if( state == null ) {
                return;
            }

            switch( state.getState() ) {
                case PlaybackState.STATE_PLAYING: {
                    mCurrentState = STATE_PLAYING;
                    break;
                }
                case PlaybackState.STATE_PAUSED: {
                    mCurrentState = STATE_PAUSED;
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayPauseToggleButton = (Button) findViewById(R.id.button);

        mMediaBrowserCompat = new MediaBrowser(this, new ComponentName(this, BackgroundAudioService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();

        mPlayPauseToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mCurrentState == STATE_PAUSED ) {
                    getMediaController().getTransportControls().play();
                    mCurrentState = STATE_PLAYING;
                } else {
                    if( getMediaController().getPlaybackState().getState() == PlaybackState.STATE_PLAYING ) {
                        getMediaController().getTransportControls().pause();
                    }

                    mCurrentState = STATE_PAUSED;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( getMediaController().getPlaybackState().getState() == PlaybackState.STATE_PLAYING ) {
            getMediaController().getTransportControls().pause();
        }

        mMediaBrowserCompat.disconnect();
    }
}
