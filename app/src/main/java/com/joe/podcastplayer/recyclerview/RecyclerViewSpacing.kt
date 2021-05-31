package com.joe.podcastplayer.recyclerview

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.*

class RecyclerViewSpacing(context: Context, verticalSpacingDp: Int, horizontalSpacingDp: Int) : RecyclerView.ItemDecoration() {
    private val density: Float = context.resources.displayMetrics.density

    private var orientation = -1
    private var spanCount = -1

    private val halfVerticalSpacing: Int = dp2px(verticalSpacingDp) / 2
    private val halfHorizontalSpacing: Int = dp2px(horizontalSpacingDp) / 2

    private var marginTop: Int = 0
    private var marginLeft: Int = 0
    private var marginRight: Int = 0
    private var marginBottom: Int = 0

    private var marginFirst = -1
    private var marginLast = -1

    private fun dp2px(dp: Int): Int = (dp * density).toInt()

    fun setMargin(marginLeftDp: Int, marginTopDp: Int, marginRightDp: Int, marginBottomDp: Int): RecyclerViewSpacing {
        this.marginLeft = dp2px(marginLeftDp)
        this.marginTop = dp2px(marginTopDp)
        this.marginRight = dp2px(marginRightDp)
        this.marginBottom = dp2px(marginBottomDp)
        return this
    }

    fun setMarginFirst(marginFirstDp: Int): RecyclerViewSpacing {
        this.marginFirst = dp2px(marginFirstDp)
        return this
    }

    fun setMarginLast(marginLastDp: Int): RecyclerViewSpacing {
        this.marginLast = dp2px(marginLastDp)
        return this
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (orientation == -1) orientation = getOrientation(parent)
        if (spanCount == -1) spanCount = getTotalSpan(parent)
        val childCount = parent.layoutManager!!.itemCount
        val childIndex = parent.getChildAdapterPosition(view)
        val itemSpanSize = getItemSpanSize(parent, childIndex)
        val spanIndex = getItemSpanIndex(parent, childIndex)
        if (spanCount < 1) return
        setSpacings(orientation, outRect, parent, childCount, childIndex, itemSpanSize, spanIndex)
    }

    private fun setSpacings(orientation: Int, outRect: Rect, parent: RecyclerView, childCount: Int, childIndex: Int, itemSpanSize: Int, spanIndex: Int) {
        outRect.top = halfVerticalSpacing
        outRect.bottom = halfVerticalSpacing
        outRect.left = halfHorizontalSpacing
        outRect.right = halfHorizontalSpacing

        val hasMarginFirst = marginFirst != -1
        val hasMarginLast = marginLast != -1

        if (isTopEdge(parent, childCount, childIndex, itemSpanSize, spanIndex)) {
            if (orientation == OrientationHelper.VERTICAL && hasMarginFirst) {
                outRect.top = marginFirst
            } else {
                outRect.top = marginTop
            }
        }

        if (isLeftEdge(parent, childCount, childIndex, itemSpanSize, spanIndex)) {
            if (orientation == OrientationHelper.HORIZONTAL && hasMarginFirst) {
                outRect.left = marginFirst
            } else {
                outRect.left = marginLeft
            }
        }

        if (isRightEdge(parent, childCount, childIndex, itemSpanSize, spanIndex)) {
            if (orientation == OrientationHelper.HORIZONTAL && hasMarginLast) {
                outRect.right = marginLast
            } else {
                outRect.right = marginRight
            }
        }

        if (isBottomEdge(parent, childCount, childIndex, itemSpanSize, spanIndex)) {
            if (orientation == OrientationHelper.VERTICAL && hasMarginLast) {
                outRect.bottom = marginLast
            } else {
                outRect.bottom = marginBottom
            }
        }
    }

    private fun getTotalSpan(parent: RecyclerView): Int {
        return when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> layoutManager.spanCount
            is StaggeredGridLayoutManager -> layoutManager.spanCount
            is LinearLayoutManager -> 1
            else -> -1
        }
    }

    private fun getItemSpanSize(parent: RecyclerView, childIndex: Int): Int {
        return when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> layoutManager.spanSizeLookup.getSpanSize(childIndex)
            is StaggeredGridLayoutManager -> 1
            is LinearLayoutManager -> 1
            else -> -1
        }
    }

    private fun getItemSpanIndex(parent: RecyclerView, childIndex: Int): Int {
        return when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> layoutManager.spanSizeLookup.getSpanIndex(childIndex, spanCount)
            is StaggeredGridLayoutManager -> childIndex % spanCount
            is LinearLayoutManager -> 0
            else -> -1
        }
    }

    private fun getOrientation(parent: RecyclerView): Int {
        return when (val layoutManager = parent.layoutManager) {
            is LinearLayoutManager -> layoutManager.orientation
            is GridLayoutManager -> layoutManager.orientation
            is StaggeredGridLayoutManager -> layoutManager.orientation
            else -> OrientationHelper.VERTICAL
        }
    }

    private fun isLeftEdge(parent: RecyclerView, childCount: Int, childIndex: Int, itemSpanSize: Int, spanIndex: Int): Boolean {
        return if (orientation == OrientationHelper.VERTICAL) {
            spanIndex == 0
        } else {
            childIndex == 0 || isFirstItemEdgeValid(childIndex < spanCount, parent, childIndex)
        }
    }

    private fun isRightEdge(parent: RecyclerView, childCount: Int, childIndex: Int, itemSpanSize: Int, spanIndex: Int): Boolean {
        return if (orientation == OrientationHelper.VERTICAL) {
            spanIndex + itemSpanSize == spanCount
        } else {
            isLastItemEdgeValid(childIndex >= childCount - spanCount, parent, childCount, childIndex, spanIndex)
        }
    }

    private fun isTopEdge(parent: RecyclerView, childCount: Int, childIndex: Int, itemSpanSize: Int, spanIndex: Int): Boolean {
        return if (orientation == OrientationHelper.VERTICAL) {
            childIndex == 0 || isFirstItemEdgeValid(childIndex < spanCount, parent, childIndex)
        } else {
            spanIndex == 0
        }
    }

    private fun isBottomEdge(parent: RecyclerView, childCount: Int, childIndex: Int, itemSpanSize: Int, spanIndex: Int): Boolean {
        return if (orientation == OrientationHelper.VERTICAL) {
            isLastItemEdgeValid(childIndex >= childCount - spanCount, parent, childCount, childIndex, spanIndex)
        } else {
            spanIndex + itemSpanSize == spanCount
        }
    }

    private fun isFirstItemEdgeValid(isOneOfFirstItems: Boolean, parent: RecyclerView, childIndex: Int): Boolean {
        var totalSpanArea = 0
        if (isOneOfFirstItems) {
            for (i in childIndex downTo 0) totalSpanArea += getItemSpanSize(parent, i)
        }
        return isOneOfFirstItems && totalSpanArea <= spanCount
    }

    private fun isLastItemEdgeValid(isOneOfLastItems: Boolean, parent: RecyclerView, childCount: Int, childIndex: Int, spanIndex: Int): Boolean {
        var totalSpanRemaining = 0
        if (isOneOfLastItems) {
            for (i in childIndex until childCount) totalSpanRemaining += getItemSpanSize(parent, i)
        }
        return isOneOfLastItems && totalSpanRemaining <= spanCount - spanIndex
    }
}
