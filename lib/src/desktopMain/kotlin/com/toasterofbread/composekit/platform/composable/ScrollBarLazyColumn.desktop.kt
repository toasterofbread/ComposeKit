package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified

@Composable
actual fun ScrollBarLazyColumn(
    modifier: Modifier,
    state: LazyListState,
    contentPadding: PaddingValues,
    reverseLayout: Boolean,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    flingBehavior: FlingBehavior,
    userScrollEnabled: Boolean,
    scrollBarColour: Color,
    reverse: Boolean,
    content: LazyListScope.() -> Unit
) {
    Row(modifier.scrollWheelScrollable(state), horizontalArrangement = Arrangement.aligned(horizontalAlignment)) {
        val scrollbar_style: ScrollbarStyle = LocalScrollbarStyle.current.run {
            if (scrollBarColour.isUnspecified) this
            else copy(hoverColor = scrollBarColour)
        }

        val scrollbar_adapter: ScrollbarAdapter = rememberScrollbarAdapter(state)

        if (reverse) {
            VerticalScrollbar(
                scrollbar_adapter,
                style = scrollbar_style
            )
        }

        LazyColumn(
            Modifier.weight(1f, false), state, contentPadding, reverseLayout, verticalArrangement, horizontalAlignment, flingBehavior, userScrollEnabled, content
        )

        if (!reverse) {
            VerticalScrollbar(
                scrollbar_adapter,
                style = scrollbar_style
            )
        }
    }
}
