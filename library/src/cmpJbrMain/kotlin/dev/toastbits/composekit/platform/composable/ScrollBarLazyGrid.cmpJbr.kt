package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.utils.modifier.horizontal
import dev.toastbits.composekit.utils.modifier.vertical

@Composable
actual fun ScrollBarLazyVerticalGrid(
    columns: GridCells,
    modifier: Modifier,
    state: LazyGridState,
    contentPadding: PaddingValues,
    reverseLayout: Boolean,
    verticalArrangement: Arrangement.Vertical,
    horizontalArrangement: Arrangement.Horizontal,
    flingBehavior: FlingBehavior,
    userScrollEnabled: Boolean,
    content: LazyGridScope.() -> Unit,
) {
    Row(modifier.padding(contentPadding.horizontal)) {
        LazyVerticalGrid(
            columns,
            Modifier.fillMaxWidth().weight(1f),
            state,
            contentPadding.vertical,
            reverseLayout,
            verticalArrangement,
            horizontalArrangement,
            flingBehavior,
            userScrollEnabled,
            content
        )
        
        VerticalScrollbar(
            rememberScrollbarAdapter(state),
            Modifier.padding(contentPadding.vertical)
        )
    }
}
