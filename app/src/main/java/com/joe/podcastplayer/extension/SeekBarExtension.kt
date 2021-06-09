package com.joe.podcastplayer.extension

import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

inline fun AppCompatSeekBar.onSeekBarChange(init: SimpleOnSeekBarChange.() -> Unit) = setOnSeekBarChangeListener(SimpleOnSeekBarChange().apply(init))

class SimpleOnSeekBarChange : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        _onChanges?.invoke(seekBar, progress, fromUser)
        if (fromUser) _onUserChanges?.invoke(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        _onStartTracking?.invoke(seekBar.progress)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        _onStopTracking?.invoke(seekBar.progress)
    }

    private var _onUserChanges: ((progress: Int) -> Unit)? = null
    private var _onChanges: ((seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit)? = null
    private var _onStartTracking: ((progress: Int) -> Unit)? = null
    private var _onStopTracking: ((progress: Int) -> Unit)? = null

    fun onUserChanges(listener: (progress: Int) -> Unit) {
        _onUserChanges = listener
    }

    fun onChanges(listener: (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit) {
        _onChanges = listener
    }

    fun onStartTracking(listener: (progress: Int) -> Unit) {
        _onStartTracking = listener
    }

    fun onStopTracking(listener: (progress: Int) -> Unit) {
        _onStopTracking = listener
    }
}
