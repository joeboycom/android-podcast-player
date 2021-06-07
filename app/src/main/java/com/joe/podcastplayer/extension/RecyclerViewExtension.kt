package com.joe.podcastplayer.extension

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.joe.podcastplayer.recyclerview.RecyclerViewSpacing

fun RecyclerView.useVerticalLayoutManager(column: Int = 1) {
    val gridLayoutManager = GridLayoutManager(context, column)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    this.layoutManager = gridLayoutManager
}

fun RecyclerView.useHorizontalLayoutManager(column: Int = 1, isLayoutRTL: Boolean = false) {
    val gridLayoutManager = object : GridLayoutManager(context, column) {
        override fun isLayoutRTL(): Boolean = isLayoutRTL
    }
    gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
    this.layoutManager = gridLayoutManager
}

fun RecyclerView.setSpacing(
    marginFirst: Int = 0,
    marginLast: Int = 0,
    verticalSpacing: Int = 0,
    horizontalSpacing: Int = 0,
    marginLeft: Int = 0,
    marginTop: Int = 0,
    marginRight: Int = 0,
    marginBottom: Int = 0
) {
    addItemDecoration(
        RecyclerViewSpacing(context, verticalSpacing, horizontalSpacing)
            .setMarginFirst(marginFirst)
            .setMarginLast(marginLast)
            .setMargin(marginLeft, marginTop, marginRight, marginBottom)
    )
}

fun RecyclerView.smoothScrollToPosition(position: Int, offset: Int) {
    val layoutManager = this.layoutManager ?: return
    when (layoutManager) {
        is StaggeredGridLayoutManager -> layoutManager.scrollToPositionWithOffset(position, offset)
        is GridLayoutManager -> layoutManager.scrollToPositionWithOffset(position, offset)
        is LinearLayoutManager -> layoutManager.scrollToPositionWithOffset(position, offset)
    }
}
