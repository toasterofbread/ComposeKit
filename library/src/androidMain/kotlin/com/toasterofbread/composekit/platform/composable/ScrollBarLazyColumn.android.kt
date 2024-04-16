package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun ScrollBarLazyColumn(
    modifier: Modifier,
    state: LazyListState,
    show_scrollbar: Boolean,
    contentPadding: PaddingValues,
    reverseLayout: Boolean,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    flingBehavior: FlingBehavior,
    userScrollEnabled: Boolean,
    scrollBarColour: Color,
    verticalAlignment: Alignment.Vertical,
    reverseScrollBarLayout: Boolean,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier,
        state,
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
        content = content
    )
}
