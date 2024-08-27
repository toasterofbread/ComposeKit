package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun ScrollabilityIndicatorRow(
    scroll_state: ScrollState,
    modifier: Modifier,
    show_start_indicator: Boolean,
    show_end_indicator: Boolean,
    scroll_amount: Float?,
    horizontal_arrangement: Arrangement.Horizontal,
    vertical_alignment: Alignment.Vertical,
    accent_colour: Color,
    content: @Composable (RowScope.() -> Unit)
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        if (show_start_indicator) {
            ScrollabilityIndicator(true, scroll_state, scroll_amount, accent_colour)
        }

        Row(
            Modifier.weight(1f).horizontalScroll(scroll_state),
            horizontalArrangement = horizontal_arrangement,
            verticalAlignment = vertical_alignment
        ) {
            content()
        }

        if (show_end_indicator) {
            ScrollabilityIndicator(false, scroll_state, scroll_amount, accent_colour)
        }
    }
}
