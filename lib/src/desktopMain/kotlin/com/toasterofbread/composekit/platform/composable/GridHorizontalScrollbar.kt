@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.foundation.v2.maxScrollOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlin.math.abs

@Composable
fun GridHorizontalScrollbar(
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    style: ScrollbarStyle = LocalScrollbarStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) = HorizontalScrollbar(
    remember(gridState) { LazyGridScrollbarAdapter(gridState) },
    modifier,
    reverseLayout,
    style,
    interactionSource
)

@Composable
fun rememberScrollbarAdapter(scrollState: LazyGridState) =
    remember(scrollState) { LazyGridScrollbarAdapter(scrollState) }

// The library grid GridScrollbarAdapter implementation doesn't seem to work at all
// Strangely, if you copy-paste the row GridScrollbarAdapter it works almost perfectly
class LazyGridScrollbarAdapter(
    private val scrollState: LazyGridState
): ScrollbarAdapter {
    class VisibleLine(
        val index: Int,
        val offset: Int
    )

    private val averageVisibleLineSize by derivedStateOf {
        if (totalLineCount() == 0)
            0.0
        else
            averageVisibleLineSize()
    }
    private val averageVisibleLineSizeWithSpacing get() = averageVisibleLineSize + lineSpacing

    override val scrollOffset: Double
        get() {
            val firstVisibleLine: VisibleLine? = firstVisibleLine()
            return if (firstVisibleLine == null)
                0.0
            else
                firstVisibleLine.index * averageVisibleLineSizeWithSpacing - firstVisibleLine.offset
        }

    override val contentSize: Double
        get() {
            val totalLineCount = totalLineCount()
            return averageVisibleLineSize * totalLineCount +
                    lineSpacing * (totalLineCount - 1).coerceAtLeast(0) +
                    contentPadding()
        }

    override val viewportSize: Double
        get() = with(scrollState.layoutInfo) {
            if (orientation == Orientation.Vertical)
                viewportSize.height
            else
                viewportSize.width
        }.toDouble()

    override suspend fun scrollTo(scrollOffset: Double) {
        val distance = scrollOffset - this@LazyGridScrollbarAdapter.scrollOffset

        // if we scroll less than viewport we need to use scrollBy function to avoid
        // undesirable scroll jumps (when an item size is different)
        //
        // if we scroll more than viewport we should immediately jump to this position
        // without recreating all items between the current and the new position
        if (abs(distance) <= viewportSize) {
            scrollBy(distance.toFloat())
        } else {
            snapTo(scrollOffset)
        }
    }

    private suspend fun snapTo(scrollOffset: Double) {
        val scrollOffsetCoerced = scrollOffset.coerceIn(0.0, maxScrollOffset)

        val index = (scrollOffsetCoerced / averageVisibleLineSizeWithSpacing)
            .toInt()
            .coerceAtLeast(0)
            .coerceAtMost(totalLineCount() - 1)

        val offset = (scrollOffsetCoerced - index * averageVisibleLineSizeWithSpacing)
            .toInt()
            .coerceAtLeast(0)

        snapToLine(lineIndex = index, scrollOffset = offset)
    }

    private fun firstFloatingVisibleItemIndex() = with(scrollState.layoutInfo.visibleItemsInfo){
        when (size) {
            0 -> null
            1 -> 0
            else -> {
                val first = this[0]
                val second = this[1]
                // If either the indices or the offsets aren't continuous, then the first item is
                // sticky, so we return 1
                if ((first.index < second.index - 1) ||
                    (first.offset.x + first.size.width + lineSpacing > second.offset.x))
                    1
                else
                    0
            }
        }
    }

    fun firstVisibleLine(): VisibleLine? {
        val firstFloatingVisibleIndex = firstFloatingVisibleItemIndex() ?: return null
        val firstFloatingItem = scrollState.layoutInfo.visibleItemsInfo[firstFloatingVisibleIndex]
        return VisibleLine(
            index = firstFloatingItem.index,
            offset = firstFloatingItem.offset.x
        )
    }

    fun totalLineCount() = scrollState.layoutInfo.totalItemsCount

    fun contentPadding() = with(scrollState.layoutInfo){
        beforeContentPadding + afterContentPadding
    }

    suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
        scrollState.scrollToItem(lineIndex, scrollOffset)
    }

    suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }

    fun averageVisibleLineSize() = with(scrollState.layoutInfo.visibleItemsInfo){
        val firstFloatingIndex = firstFloatingVisibleItemIndex() ?: return@with 0.0
        val first = this[firstFloatingIndex]
        val last = last()
        val count = size - firstFloatingIndex
        (last.offset.x + last.size.width - first.offset.x - (count-1)*lineSpacing).toDouble() / count
    }

    val lineSpacing get() = scrollState.layoutInfo.mainAxisItemSpacing
}
