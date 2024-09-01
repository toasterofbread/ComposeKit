package dev.toastbits.composekit.utils.composable

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import dev.toastbits.composekit.platform.composable.*
import dev.toastbits.composekit.utils.common.thenIf
import dev.toastbits.composekit.utils.modifier.horizontal
import dev.toastbits.composekit.utils.modifier.vertical
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
    scrollable: Boolean = false,
    content_padding: PaddingValues = PaddingValues(),
    content: @Composable RowOrColumnScope.() -> Unit
) {
    if (row) {
        Row(
            modifier
                .padding(content_padding.vertical)
                .thenIf(scrollable) {
                    horizontalScroll(rememberScrollState())
                },
            horizontalArrangement = arrangement,
            verticalAlignment = alignment.toVerticalAlignment()
        ) {
            val layout_direction: LayoutDirection = LocalLayoutDirection.current
            Spacer(Modifier.width(content_padding.calculateStartPadding(layout_direction)))
            content(RowScopeRowOrColumnScope(this@Row))
            Spacer(Modifier.width(content_padding.calculateEndPadding(layout_direction)))
        }
    }
    else {
        Column(
            modifier
                .padding(content_padding.horizontal)
                .thenIf(scrollable) {
                    verticalScroll(rememberScrollState())
                },
            verticalArrangement = arrangement,
            horizontalAlignment = alignment.toHorizontalAlignment()
        ) {
            Spacer(Modifier.height(content_padding.calculateTopPadding()))
            content(ColumnScopeRowOrColumnScope(this@Column))
            Spacer(Modifier.height(content_padding.calculateBottomPadding()))
        }
    }
}

@Composable
fun ScrollableRowOrColumn(
    row: Boolean,
    lazy: Boolean,
    item_count: Int,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(0.dp),
    alignment: Int = 0,
    content_padding: PaddingValues = PaddingValues(),
    reverse_scroll_bar_layout: Boolean = false,
    scroll_bar_colour: Color = Color.Unspecified,
    itemContent: @Composable (Int) -> Unit
) {
    if (lazy) {
        ScrollBarLazyRowOrColumn(
            row = row,
            modifier = modifier,
            arrangement = arrangement,
            alignment = alignment,
            contentPadding = content_padding,
            reverseScrollBarLayout = reverse_scroll_bar_layout,
            scrollBarColour = scroll_bar_colour
        ) {
            items(item_count) {
                itemContent(it)
            }
        }
    }
    else {
        RowOrColumn(
            row = row,
            modifier = modifier,
            arrangement = arrangement,
            alignment = alignment,
            scrollable = true,
            content_padding = content_padding
        ) {
            for (i in 0 until item_count) {
                itemContent(i)
            }
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
    scrollBarSpacing: Dp = 5.dp,
    rowOrColumnModifier: Modifier = Modifier,
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
            scrollBarSpacing,
            rowOrColumnModifier,
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
            scrollBarSpacing,
            rowOrColumnModifier,
            content = content
        )
    }
}

fun Int.toVerticalAlignment(): Alignment.Vertical =
    when (sign) {
        -1 -> Alignment.Top
        0 -> Alignment.CenterVertically
        else -> Alignment.Bottom
    }

fun Int.toHorizontalAlignment(): Alignment.Horizontal =
    when (sign) {
        -1 -> Alignment.Start
        0 -> Alignment.CenterHorizontally
        else -> Alignment.End
    }

class RowScopeRowOrColumnScope(val row_scope: RowScope): RowOrColumnScope() {
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

class ColumnScopeRowOrColumnScope(val column_scope: ColumnScope): RowOrColumnScope() {
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
