package com.toasterofbread.composekit.utils.composable

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.animation.*
import com.toasterofbread.composekit.platform.composable.*
import kotlin.math.sign

abstract class RowOrColumnScope {
    abstract fun Modifier.weight(weight: Float, fill: Boolean = false): Modifier

    @Composable
    fun AnimatedVisibility(
        visible: Boolean,
        modifier: Modifier = Modifier,
        enter: EnterTransition = fadeIn(),
        exit: ExitTransition = fadeOut(),
        label: String = "AnimatedVisibility",
        content: @Composable AnimatedVisibilityScope.() -> Unit
    ) {
        AnimatedVisibilityImpl(visible, modifier, enter, exit, label, content)
    }

    @Composable
    protected abstract fun AnimatedVisibilityImpl(
        visible: Boolean,
        modifier: Modifier,
        enter: EnterTransition,
        exit: ExitTransition,
        label: String,
        content: @Composable AnimatedVisibilityScope.() -> Unit
    )
}

@Composable
fun RowOrColumn(
    row: Boolean,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceEvenly,
    alignment: Int = 0,
    content: @Composable RowOrColumnScope.() -> Unit
) {
    if (row) {
        Row(
            modifier,
            horizontalArrangement = arrangement,
            verticalAlignment = alignment.toVerticalAlignment()
        ) {
            content(RowScopeRowOrColumnScope(this@Row))
        }
    }
    else {
        Column(
            modifier,
            verticalArrangement = arrangement,
            horizontalAlignment = alignment.toHorizontalAlignment()
        ) {
            content(ColumnScopeRowOrColumnScope(this@Column))
        }
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

private class RowScopeRowOrColumnScope(val row_scope: RowScope): RowOrColumnScope() {
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier =
        with (row_scope) {
            weight(weight, fill)
        }

    @Composable
    override fun AnimatedVisibilityImpl(
        visible: Boolean,
        modifier: Modifier,
        enter: EnterTransition,
        exit: ExitTransition,
        label: String,
        content: @Composable AnimatedVisibilityScope.() -> Unit
    ) {
        row_scope.AnimatedVisibility(visible, modifier, enter, exit, label, content)
    }
}

private class ColumnScopeRowOrColumnScope(val column_scope: ColumnScope): RowOrColumnScope() {
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier =
        with (column_scope) {
            weight(weight, fill)
        }

    @Composable
    override fun AnimatedVisibilityImpl(
        visible: Boolean,
        modifier: Modifier,
        enter: EnterTransition,
        exit: ExitTransition,
        label: String,
        content: @Composable AnimatedVisibilityScope.() -> Unit
    ) {
        column_scope.AnimatedVisibility(visible, modifier, enter, exit, label, content)
    }
}
