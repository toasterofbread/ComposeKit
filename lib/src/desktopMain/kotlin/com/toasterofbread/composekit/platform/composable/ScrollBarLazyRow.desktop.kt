package com.toasterofbread.composekit.platform.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.toasterofbread.composekit.utils.common.thenIf

@Composable
actual fun ScrollBarLazyRow(
    modifier: Modifier,
    state: LazyListState,
    show_scrollbar: Boolean,
    contentPadding: PaddingValues,
    reverseLayout: Boolean,
    horizontalArrangement: Arrangement.Horizontal,
    verticalAlignment: Alignment.Vertical,
    flingBehavior: FlingBehavior,
    userScrollEnabled: Boolean,
    scrollBarColour: Color,
    horizontalAlignment: Alignment.Horizontal,
    reverseScrollBarLayout: Boolean,
    content: LazyListScope.() -> Unit
) {
    Column(modifier.scrollWheelScrollable(state), horizontalAlignment = horizontalAlignment) {
        val scrollbar_alpha: Float by animateFloatAsState(if (state.isContentOverflowing()) 1f else 0f)

        val scrollbar_height: Dp = 10.dp
        val scrollbar_padding: Dp = 5.dp
        val scrollbar_modifier: Modifier = Modifier.padding(bottom = scrollbar_padding).height(scrollbar_height - scrollbar_padding).alpha(scrollbar_alpha)

        val scrollbar_style: ScrollbarStyle = LocalScrollbarStyle.current.run {
            if (scrollBarColour.isUnspecified) this
            else copy(
                hoverColor = scrollBarColour,
                unhoverColor = scrollBarColour.copy(alpha = scrollBarColour.alpha * 0.25f)
            )
        }

        if (reverseScrollBarLayout && show_scrollbar) {
            HorizontalScrollbar(
                rememberScrollbarAdapter(state),
                scrollbar_modifier,
                style = scrollbar_style
            )
        }

        LazyRow(
            Modifier
                .weight(1f, false)
                .thenIf(show_scrollbar) {
                    offset(y = (scrollbar_height / 2) * (1f - scrollbar_alpha))
                },
            state,
            contentPadding,
            reverseLayout,
            horizontalArrangement,
            verticalAlignment,
            flingBehavior,
            userScrollEnabled
        ) {
            content()
        }

        if (!reverseScrollBarLayout && show_scrollbar) {
            HorizontalScrollbar(
                rememberScrollbarAdapter(state),
                scrollbar_modifier,
                style = scrollbar_style
            )
        }
    }
}

private fun LazyListState.isContentOverflowing(): Boolean {
    val last_item: LazyListItemInfo? = layoutInfo.visibleItemsInfo.lastOrNull()
    return last_item != null && (
        last_item.index < layoutInfo.totalItemsCount - 1
        || (last_item.offset + last_item.size) + 5 >= layoutInfo.viewportEndOffset
    )
}
