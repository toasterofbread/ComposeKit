package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.Color

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
        val scrollbar_style: ScrollbarStyle = LocalScrollbarStyle.current.run {
            if (scrollBarColour.isUnspecified) this
            else copy(hoverColor = scrollBarColour)
        }

        if (reverseScrollBarLayout) {
            HorizontalScrollbar(
                rememberScrollbarAdapter(state),
                Modifier.padding(bottom = 5.dp),
                style = scrollbar_style
            )
        }

        LazyRow(
            Modifier, state, contentPadding, reverseLayout, horizontalArrangement, verticalAlignment, flingBehavior, userScrollEnabled, content
        )

        if (!reverseScrollBarLayout && show_scrollbar) {
            HorizontalScrollbar(
                rememberScrollbarAdapter(state),
                Modifier.padding(bottom = 5.dp),
                style = scrollbar_style
            )
        }
    }
}
