package com.toasterofbread.composekit.platform.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ScrollabilityIndicatorColumn(
    scroll_state: ScrollableState,
    modifier: Modifier = Modifier,
    show_up_indicator: Boolean = false,
    scroll_amount: Float? = with(LocalDensity.current) { 50.dp.toPx() },
    content: @Composable () -> Unit
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (show_up_indicator) {
            ScrollabilityIndicator(true, scroll_state, scroll_amount)
        }

        Box(Modifier.weight(1f)) {
            content()
        }

        ScrollabilityIndicator(false, scroll_state, scroll_amount)
    }
}


@Composable
expect fun ScrollabilityIndicatorRow(
    scroll_state: ScrollState,
    modifier: Modifier = Modifier,
    show_start_indicator: Boolean = false,
    show_end_indicator: Boolean = true,
    scroll_amount: Float? = with(LocalDensity.current) { 50.dp.toPx() },
    horizontal_arrangement: Arrangement.Horizontal = Arrangement.Start,
    vertical_alignment: Alignment.Vertical = Alignment.Top,
    accent_colour: Color = LocalContentColor.current,
    content: @Composable() (RowScope.() -> Unit)
)

@Composable
fun ColumnScope.ScrollabilityIndicator(up: Boolean, list_state: ScrollableState, scroll_amount: Float? = null) {
    val coroutine_scope = rememberCoroutineScope()
    val show = if (up) list_state.canScrollBackward else list_state.canScrollForward
    val icon = if (up) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown

    Box(
        Modifier
            .height(24.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (scroll_amount != null) {
                    coroutine_scope.launch {
                        list_state.animateScrollBy(if (up) -scroll_amount else scroll_amount)
                    }
                }
            }
    ) {
        this@ScrollabilityIndicator.AnimatedVisibility(show) {
            Icon(icon, null)
        }
    }
}

@Composable
fun RowScope.ScrollabilityIndicator(
    left: Boolean,
    list_state: ScrollableState,
    scroll_amount: Float? = null,
    colour: Color = LocalContentColor.current
) {
    val coroutine_scope: CoroutineScope = rememberCoroutineScope()
    val show: Boolean = if (left) list_state.canScrollBackward else list_state.canScrollForward
    val icon: ImageVector = if (left) Icons.AutoMirrored.Default.KeyboardArrowLeft else Icons.AutoMirrored.Default.KeyboardArrowRight

    Box(
        Modifier
            .width(24.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (scroll_amount != null) {
                    coroutine_scope.launch {
                        list_state.animateScrollBy(if (left) -scroll_amount else scroll_amount)
                    }
                }
            }
    ) {
        this@ScrollabilityIndicator.AnimatedVisibility(show) {
            Icon(icon, null, tint = colour)
        }
    }
}

