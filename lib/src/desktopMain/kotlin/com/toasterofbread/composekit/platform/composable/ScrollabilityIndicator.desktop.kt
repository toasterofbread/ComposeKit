package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    Column(modifier.scrollWheelScrollable(scroll_state)) {
        Row(
            Modifier.horizontalScroll(scroll_state),
            horizontalArrangement = horizontal_arrangement,
            verticalAlignment = vertical_alignment
        ) {
            content()
        }

        HorizontalScrollbar(
            rememberScrollbarAdapter(scroll_state),
            Modifier.padding(vertical = 5.dp),
            style = LocalScrollbarStyle.current.copy(
                hoverColor = accent_colour
            )
        )
    }
}
