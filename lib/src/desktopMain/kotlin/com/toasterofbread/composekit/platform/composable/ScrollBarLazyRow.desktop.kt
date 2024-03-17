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
