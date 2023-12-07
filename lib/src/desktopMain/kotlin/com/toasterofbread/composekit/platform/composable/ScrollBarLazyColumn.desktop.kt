package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    content: LazyListScope.() -> Unit
) {
    Row(modifier.scrollWheelScrollable(state)) {
        LazyColumn(
            Modifier.weight(1f), state, contentPadding, reverseLayout, verticalArrangement, horizontalAlignment, flingBehavior, userScrollEnabled, content
        )

        VerticalScrollbar(
            rememberScrollbarAdapter(state),
            Modifier.padding(bottom = 0.dp)
        )
    }
}
