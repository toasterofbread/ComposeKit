package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.utils.common.thenIf
import kotlin.math.roundToInt

@Composable
fun Marquee(modifier: Modifier = Modifier, arrangement: Arrangement.Horizontal = Arrangement.Start, disable: Boolean = false, content: @Composable () -> Unit) {
    MeasureUnconstrainedView(content) { content_size ->
        val scroll_state: ScrollState = rememberScrollState()

        val density: Density = LocalDensity.current
        var container_width: Int by remember { mutableStateOf(0) }

        LaunchedEffect(scroll_state.isScrollInProgress) {
            val max_scroll: Int = content_size.width - container_width
            if (scroll_state.value > max_scroll) {
                scroll_state.scrollTo(max_scroll)
            }
        }

        val scroll_value: Dp by remember {
            derivedStateOf {
                with(density) {
                    if (container_width >= content_size.width) {
                        0.dp
                    } 
                    else {
                        (-scroll_state.value).coerceIn(container_width - content_size.width, 0).toDp()
                    }
                }
            }
        }

        Row(
            modifier
                .scrollable(
                    scroll_state,
                    Orientation.Horizontal,
                    reverseDirection = true
                )
                .fillMaxWidth()
                .clipToBounds()
                .onSizeChanged {
                    container_width = it.width
                }
        ) {
            Row(
                Modifier
                    .thenIf(!disable) {
                        requiredWidth(with(density) { container_width.toDp() - scroll_value })
                        .offset { IntOffset((scroll_value / 2).toPx().roundToInt(), 0) }
                    },
                horizontalArrangement = arrangement
            ) {
                content()
            }
        }
    }
}
