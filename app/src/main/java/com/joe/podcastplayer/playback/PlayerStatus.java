package com.joe.podcastplayer.playback;

public enum PlayerStatus {
    INDETERMINATE(0),  // player is currently changing its state, listeners should wait until the player has left this state.
	ERROR(-1),
	PREPARING(19),
	PAUSED(30),
	PLAYING(40),
	STOPPED(5),
	PREPARED(20),
	SEEKING(29),
	INITIALIZING(9),			// playback service is loading the Playable's metadata
	INITIALIZED(10),	// playback service was started, data source of media player was set.

	IDLE(11),				// 1.new MediaPlayer 時的狀態, 2. MediaPlayer reset()
//	INITIALIZED,		// 準備狀態時為INITIALIZED 避免使用者作 progress bar 拖拉操作, 以及 start pause 操作 ( MediaPlayer.OnPreparedListener)
//	PREPARING, 			// MediaPlayer prepareAsync()
//	PREPARED,
	STARTED(22),			// MediaPlayer 播放狀態
//	STOPPED, 			// MediaPlayer 停止狀態
//	PAUSED, 			// MediaPlayer 暫停狀態
	PLAYBACK_COMPLETED(33), // 重播
//	ERROR,
	END(44);					// MediaPlayer release()

	private final int statusValue;
    private static final PlayerStatus[] fromOrdinalLookup;

    static {
        fromOrdinalLookup = PlayerStatus.values();
    }

	PlayerStatus(int val) {
		statusValue = val;
	}

    public static PlayerStatus fromOrdinal(int o) {
        return fromOrdinalLookup[o];
    }

	public boolean isAtLeast(PlayerStatus other) {
		return other == null || this.statusValue>=other.statusValue;
	}
}
