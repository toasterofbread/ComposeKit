package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.toastbits.composekit.platform.Platform
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.composekit.utils.common.thenWith
import dev.toastbits.composekit.utils.common.copy
import dev.toastbits.composekit.utils.common.thenIf
import dev.toastbits.composekit.utils.modifier.background

@Composable
fun ScrollBarLazyColumnWithHeader(
    header_index: Int,
    headerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    show_scrollbar: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    scrollBarColour: Color = Color.Unspecified,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    reverseScrollBarLayout: Boolean = false,
    getHeaderBackgroundColour: (() -> Color)? = null,
    content: LazyListScope.(headerContent: @Composable () -> Unit) -> Unit
) {
    val show_header: Boolean by remember { derivedStateOf { state.firstVisibleItemIndex >= header_index } }
    val opaque_header: Boolean by remember { derivedStateOf { state.firstVisibleItemIndex > header_index } }

    Box(modifier) {
        if (show_header) {
            Box(
                Modifier
                    .thenIf(Platform.DESKTOP.isCurrent()) {
                        if (reverseLayout) padding(start = 7.dp)
                        else padding(end = 7.dp)
                    }
                    .thenIf(!opaque_header) {
                        padding(contentPadding.copy(bottom = 0.dp))
                    }
                    .thenWith(getHeaderBackgroundColour) {
                        background(it)
                    }
                    .thenIf(opaque_header) {
                        padding(contentPadding.copy(bottom = 0.dp))
                    }
                    .zIndex(1f)
            ) {
                headerContent()
            }
        }

        ScrollBarLazyColumn(
            Modifier,
            state,
            show_scrollbar,
            contentPadding,
            reverseLayout,
            verticalArrangement,
            horizontalAlignment,
            flingBehavior,
            userScrollEnabled,
            scrollBarColour,
            verticalAlignment,
            reverseScrollBarLayout
        ) {
            content {
                Box(
                    Modifier
                        .graphicsLayer {
                            alpha = if (show_header) 0f else 1f
                        }
                ) {
                    headerContent()
                }
            }
        }
    }
}
