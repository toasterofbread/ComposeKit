package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import dev.toastbits.composekit.utils.composable.RowScopeRowOrColumnScope
import dev.toastbits.composekit.utils.composable.toVerticalAlignment
import dev.toastbits.composekit.utils.composable.toHorizontalAlignment

@Composable
fun StickyLengthRowOrColumn(
    row: Boolean,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceEvenly,
    alignment: Int = 0,
    on_axis_alignment: Int? = null,
    key: Any? = Unit,
    content: @Composable RowOrColumnScope.() -> Unit
) {
    if (row) {
        StickyWidthRow(
            modifier,
            horizontalArrangement =
                if (on_axis_alignment != null) Arrangement.spacedBy(0.dp, on_axis_alignment.toHorizontalAlignment())
                else arrangement,
            verticalAlignment = alignment.toVerticalAlignment()
        ) {
            content(RowScopeRowOrColumnScope(this))
        }
    }
    else {
        StickyHeightColumn(
            modifier,
            verticalArrangement =
                if (on_axis_alignment != null) Arrangement.spacedBy(0.dp, on_axis_alignment.toVerticalAlignment())
                else arrangement,
            horizontalAlignment = alignment.toHorizontalAlignment()
        ) {
            content(ColumnScopeRowOrColumnScope(this))
        }
    }
}

@Composable
fun WithStickySize(
    key: Any? = Unit,
    content: @Composable (Modifier, DpSize) -> Unit
) {
    val density: Density = LocalDensity.current
    var largest_width: Dp by remember(key) { mutableStateOf(0.dp) }
    var largest_height: Dp by remember(key) { mutableStateOf(0.dp) }

    content(
        Modifier
            .onSizeChanged {
                with(density) {
                    largest_width = maxOf(largest_width, it.width.toDp())
                    largest_height = maxOf(largest_height, it.height.toDp())
                }
            }
            .widthIn(min = largest_width),
        DpSize(largest_width, largest_height)
    )
}

@Composable
fun StickyWidthRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    key: Any? = Unit,
    content: @Composable RowScope.() -> Unit
) {
    WithStickySize(key) { size_modifier, size ->
        Row(
            modifier
                .then(size_modifier)
                .widthIn(min = size.width),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment,
            content = content
        )
    }
}

@Composable
fun StickyHeightColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    key: Any? = Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    WithStickySize(key) { size_modifier, size ->
        Column(
            modifier
                .then(size_modifier)
                .heightIn(min = size.height),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}
