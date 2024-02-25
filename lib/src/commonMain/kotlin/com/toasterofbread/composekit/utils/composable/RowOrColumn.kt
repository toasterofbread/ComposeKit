package com.toasterofbread.composekit.utils.composable

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import kotlin.math.sign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import com.toasterofbread.composekit.platform.composable.ScrollBarLazyRow
import com.toasterofbread.composekit.platform.composable.ScrollBarLazyColumn
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.ui.graphics.Color

private fun Int.toVerticalAlignment(): Alignment.Vertical =
    when (sign) {
        -1 -> Alignment.Top
        0 -> Alignment.CenterVertically
        else -> Alignment.Bottom
    }

private fun Int.toHorizontalAlignment(): Alignment.Horizontal =
    when (sign) {
        -1 -> Alignment.Start
        0 -> Alignment.CenterHorizontally
        else -> Alignment.End
    }

@Composable
fun RowOrColumn(
    row: Boolean,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceEvenly,
    alignment: Int = 0,
    content: @Composable (getWeightModifier: (Float) -> Modifier) -> Unit,
) {
    if (row) {
        Row(
            modifier,
            horizontalArrangement = arrangement,
            verticalAlignment = alignment.toVerticalAlignment()
        ) { content { Modifier.weight(it) } }
    }
    else {
        Column(
            modifier,
            verticalArrangement = arrangement,
            horizontalAlignment = alignment.toHorizontalAlignment()
        ) { content { Modifier.weight(it) } }
    }
}

@Composable
fun ScrollBarLazyRowOrColumn(
    row: Boolean,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    show_scrollbar: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceEvenly,
    alignment: Int = 0,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    scrollBarColour: Color = Color.Unspecified,
    alt_alignment: Int = -1,
    reverseScrollBarLayout: Boolean = false,
    content: LazyListScope.() -> Unit
) {
    if (row) {
        ScrollBarLazyRow(
            modifier,
            state,
            show_scrollbar,
            contentPadding,
            reverseLayout,
            arrangement,
            alignment.toVerticalAlignment(),
            flingBehavior,
            userScrollEnabled,
            scrollBarColour,
            alt_alignment.toHorizontalAlignment(),
            reverseScrollBarLayout,
            content = content
        )
    }
    else {
        ScrollBarLazyColumn(
            modifier,
            state,
            show_scrollbar,
            contentPadding,
            reverseLayout,
            arrangement,
            alignment.toHorizontalAlignment(),
            flingBehavior,
            userScrollEnabled,
            scrollBarColour,
            alt_alignment.toVerticalAlignment(),
            reverseScrollBarLayout,
            content = content
        )
    }
}
