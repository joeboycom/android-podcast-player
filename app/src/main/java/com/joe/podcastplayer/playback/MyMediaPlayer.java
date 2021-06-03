package com.joe.podcastplayer.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

public class MyMediaPlayer extends MediaPlayer implements	MediaPlayer.OnPreparedListener, 
															MediaPlayer.OnCompletionListener,
															MediaPlayer.OnErrorListener, 
															MediaPlayer.OnBufferingUpdateListener 
{
	public static final int USAGE_STATUS_CURRENT_MEDIA_YES = 1;
	public static final int USAGE_STATUS_CURRENT_MEDIA_NO  = 0;
	
	//implement position update listener
	public interface OnPositionUpdateListener 
	{
		public void onPositionUpdate(MyMediaPlayer mmp, int position);
	}	

	//implement state change listener
	public interface OnStateChangedListener 
	{
		public void onStateChanged(MyMediaPlayer mmp, PlayerStatus state);
	}
	
	private static final String TAG = MyMediaPlayer.class.getName();
	public 	static final int MEDIA_ERROR_3PM 		= 12345;
	public 	static final int EXTRA_DATA_NOT_FOUND 	= 0x01;
	public 	static final int EXTRA_IO_EXCEPTION 	= 0x02;
	public 	static final int EXTRA_UNKNOWN 			= 0x03;
	
	public 	static final String CACHE_DIR 			= "songs";
		
	private OnCompletionListener 		mOnCompletionListener;
	private OnBufferingUpdateListener 	mOnBufferingUpdateListener;
	private OnPreparedListener 			mOnPreparedListener;
	private OnErrorListener 			mOnErrorListener;
	private OnPositionUpdateListener 				mOnPositionUpdateListener;
	private OnStateChangedListener 					mOnStateChangedListener;

	private Context 								mContext;
	private int 									mUsageStatus;
	private int 									mBufferingPercent;
	private boolean 								mIsStreamMusic;
		
	private PlayerStatus 									mState;					//MediaPlayer 狀態
	private int 									mStartPosition;
	private String 									mMP3Path;
	private boolean 								mBuffering;

	private Handler 								mMyMediaPlayerHandler = new Handler();

	public MyMediaPlayer(Context context, String path) 
	{
		super();
		super.setOnPreparedListener(this);
		super.setOnCompletionListener(this);
		super.setOnBufferingUpdateListener(this);
		super.setOnErrorListener(this);
		
		// To ensure that the CPU continues running while your MediaPlayer is playing
		setWakeMode(context.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		
		mContext 			= context;
		mMP3Path 			= path;		
		setUsageStatus(USAGE_STATUS_CURRENT_MEDIA_NO);
		mBufferingPercent 	= 0;
		mBuffering 			= false;
		
		setState(PlayerStatus.IDLE);
	}

	@Override
	public void start() 
	{
		super.start();
		Log.e(TAG, "start");
		setState(PlayerStatus.STARTED);	// Note it cannot trigger state changed using setNextMediaPlayer()
	}

	@Override
	public void stop() 
	{
		super.stop();
		Log.e(TAG, "stop");
		setState(PlayerStatus.STOPPED);
	}

	@Override
	public void pause() 
	{
		super.pause();
		Log.e(TAG, "pause");
		setState(PlayerStatus.PAUSED);
	}
	
	@Override
	public void reset() 
	{		
		super.reset();
		Log.e(TAG, "reset");
		
		setUsageStatus(USAGE_STATUS_CURRENT_MEDIA_NO);
		mBufferingPercent 	= 0;
		mBuffering 			= false;
		mIsStreamMusic 		= false;
		mStartPosition 		= 0;
		setState(PlayerStatus.IDLE);
		
		mMyMediaPlayerHandler.removeCallbacks(mPositionUpdateRunnable);
	}
		
	@Override
	public void prepareAsync() 
	{
		super.prepareAsync();
		Log.e(TAG, "prepareAsync");
		
		mBufferingPercent = 0;
		setState(PlayerStatus.PREPARING);
	}
	
	@Override
	public void release() 
	{
		super.release();

		setState(PlayerStatus.END);
			
		mMyMediaPlayerHandler.removeCallbacks(mPositionUpdateRunnable);
	}
	
	@Override
	public void setAudioStreamType(int streamType) 
	{
		super.setAudioStreamType(streamType);
		if (streamType == AudioManager.STREAM_MUSIC) 
			mIsStreamMusic = true;
	}
	
	@Override
	public void seekTo(int msec) 
	{
		if (mState == PlayerStatus.PREPARED 			||
			mState == PlayerStatus.STARTED			||
			mState == PlayerStatus.PAUSED				||
			mState == PlayerStatus.PLAYBACK_COMPLETED)
		{
			super.seekTo(msec);
		
			Log.e(TAG, "seekTo()" + String.valueOf(msec));
			
			if (mOnPositionUpdateListener != null) 
				mOnPositionUpdateListener.onPositionUpdate(this, msec);
		}
	}

//========================================================================================================
// 撥放器狀態 回呼通知
//========================================================================================================
	@Override
	public void onPrepared(MediaPlayer mp) 
	{
		if (!mIsStreamMusic) 
			mBufferingPercent = 100;
		
		setState(PlayerStatus.PREPARED);
		
		if (mOnPreparedListener != null) 
			mOnPreparedListener.onPrepared(mp);
		
		mMyMediaPlayerHandler.removeCallbacks(mPositionUpdateRunnable);
		mMyMediaPlayerHandler.post(mPositionUpdateRunnable);
		
		if (mStartPosition > 0) 
		{
			Log.e(TAG, "onPrepared mStartPosition" + String.valueOf(mStartPosition));
			try 
			{
				seekTo(mStartPosition);
			} 
			catch (IllegalStateException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private Runnable mPositionUpdateRunnable = new Runnable() 
	{
		@Override
		public void run() 
		{
			try 
			{
				if (isPlaying() && mOnPositionUpdateListener != null) 
					mOnPositionUpdateListener.onPositionUpdate(MyMediaPlayer.this, getCurrentPosition());
			} 
			catch (Exception e) 
			{
				Log.w(TAG, e.toString());
			}

			mMyMediaPlayerHandler.postDelayed(this, 500);
		}
	};
	
	@Override
	public void onCompletion(MediaPlayer mp) 
	{		
		Log.e(TAG, "onCompletion");
		
		setState(PlayerStatus.PLAYBACK_COMPLETED);
		
		if (mOnCompletionListener != null) 
			mOnCompletionListener.onCompletion(mp);
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) 
	{	
		setState(PlayerStatus.ERROR);
		
		if (mOnErrorListener != null) 
			return mOnErrorListener.onError(mp, what, extra);
		else 
			return false;
	}
	
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) 
	{
		mBufferingPercent = percent;

		if (mOnBufferingUpdateListener != null) 
			mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
	}
	
	@Override
	public int getDuration()
	{
		if (mState == PlayerStatus.IDLE 			||
			mState == PlayerStatus.INITIALIZED	||
			mState == PlayerStatus.ERROR)
			return 0;
		
		return super.getDuration();
		
	}
		
	@Override
	public void setOnPreparedListener(OnPreparedListener li) 							{	mOnPreparedListener 		= li;	}
	@Override
	public void setOnCompletionListener(OnCompletionListener li) 						{	mOnCompletionListener 		= li;	}
	@Override
	public void setOnErrorListener(OnErrorListener li) 									{	mOnErrorListener 			= li;	}
	@Override
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener li) 				{	mOnBufferingUpdateListener 	= li;	}
	
	public void setOnPositionUpdateListener(OnPositionUpdateListener li)				{	mOnPositionUpdateListener 	= li;	}

	public void setOnStateChangedListener(OnStateChangedListener li)					{	mOnStateChangedListener 	= li;	}

//========================================================================================================
// 播歌
//========================================================================================================

	// 直接播 URL位置
	private void setDataSourceViaProxy(String url) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException 
	{
		Log.e(TAG, "setDataSourceViaProxy");

		if (mState == PlayerStatus.IDLE)
		{
			mIsStreamMusic = true;
			super.setDataSource(url);
			prepareAsync();
		}
	}

	public void setStartPosition(int position)
	{
		mStartPosition = position;
	}

	synchronized public int getUsageStatus()
	{
		return mUsageStatus;
	}

	synchronized public void setUsageStatus(int usageStatus)
	{
		mUsageStatus = usageStatus;
	}

	public boolean getBuffering() 
	{
		return mBuffering;
	}
	
	public void setBuffering(boolean buffering) 
	{
		mBuffering = buffering;
	}
	
	public int getBufferingPercent() 
	{
		return mBufferingPercent;
	}
		
	public PlayerStatus getState()
	{
		return mState;
	}

	public void setState(PlayerStatus state)
	{
		mState = state;

		Log.e(TAG, "setState: " + state.name());

		if (mOnStateChangedListener != null)
			mOnStateChangedListener.onStateChanged(this, mState);
	}
}