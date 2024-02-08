package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
    LazyVerticalGrid(
        columns,
        modifier,
        state,
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalArrangement,
        flingBehavior,
        userScrollEnabled,
        content
    )
}
