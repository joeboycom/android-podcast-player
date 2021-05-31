package com.joe.podcastplayer.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

abstract class RecyclerPagerScrollListener : RecyclerView.OnScrollListener() {
    private val MINIMUM = 25f
    private var scrollDist = 0
    private var isVisible = true
    private var dy = 0
    private val canReturnScrolling = true
    private var firstVisibleItem: Int = 0
    private val visibleItemCount: Int = 0
    private val totalItemCount: Int = 0
    private var linearLayoutManager: LinearLayoutManager? = null

    val isScrolling: Boolean
        get() = abs(dy) > 1

    fun setLinearLayoutManager(linearLayoutManager: LinearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!canReturnScrolling) {
            return
        }
        this.dy = dy
        if (linearLayoutManager != null) {
            if (firstVisibleItem != linearLayoutManager!!.findFirstVisibleItemPosition()) {
                firstVisibleItem = linearLayoutManager!!.findFirstVisibleItemPosition()
                onPositionChanged(
                    linearLayoutManager!!.findFirstVisibleItemPosition(),
                    recyclerView.childCount,
                    linearLayoutManager!!.itemCount
                )
            }
        }

        if (isVisible && scrollDist > MINIMUM) {
            onScrollUp()
            scrollDist = 0
            isVisible = false
        } else if (!isVisible && scrollDist < -MINIMUM) {
            onScrollDown()
            scrollDist = 0
            isVisible = true
        }
        if (isVisible && dy > 0 || !isVisible && dy < 0) {
            scrollDist += dy
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    abstract fun onScrollUp()

    abstract fun onScrollDown()

    abstract fun onPositionChanged(firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int)

    companion object {
        var TAG = RecyclerPagerScrollListener::class.java.simpleName
    }
}
