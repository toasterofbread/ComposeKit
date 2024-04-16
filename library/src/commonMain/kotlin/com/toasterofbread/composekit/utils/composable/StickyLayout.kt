package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment

@Composable
fun StickyHeightColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val density: Density = LocalDensity.current
    var largest_height: Dp by remember { mutableStateOf(0.dp) }

    Column(
        modifier
            .onSizeChanged {
                largest_height = with(density) {
                    maxOf(largest_height, it.height.toDp())
                }
            }
            .heightIn(min = largest_height),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}
