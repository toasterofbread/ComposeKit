package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import dev.toastbits.composekit.utils.modifier.*
import dev.toastbits.composekit.utils.common.thenIf
import kotlinx.coroutines.*

private const val ARROW_KEY_SCROLL_AMOUNT: Float = 75f

@Composable
actual fun ScrollBarLazyColumn(
    modifier: Modifier,
    state: LazyListState,
    show_scrollbar: Boolean,
    contentPadding: PaddingValues,
    reverseLayout: Boolean,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    flingBehavior: FlingBehavior,
    userScrollEnabled: Boolean,
    scrollBarColour: Color,
    verticalAlignment: Alignment.Vertical,
    reverseScrollBarLayout: Boolean,
    content: LazyListScope.() -> Unit
) {
    val density: Density = LocalDensity.current
    val focus_requester: FocusRequester = remember { FocusRequester() }
    val coroutine_scope: CoroutineScope = rememberCoroutineScope()

    var height: Int by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        focus_requester.requestFocus()
    }

    Row(
        modifier
            .onSizeChanged {
                height = it.height
            }
            .thenIf(userScrollEnabled) {
                scrollWheelScrollable(state)
            }
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.key) {
                        Key.DirectionUp -> coroutine_scope.launch {
                            state.animateScrollBy(-ARROW_KEY_SCROLL_AMOUNT)
                        }
                        Key.DirectionDown -> coroutine_scope.launch {
                            state.animateScrollBy(ARROW_KEY_SCROLL_AMOUNT)
                        }
                        Key.PageUp -> coroutine_scope.launch {
                            state.animateScrollBy(-height.toFloat())
                        }
                        Key.PageDown -> coroutine_scope.launch {
                            state.animateScrollBy(height.toFloat())
                        }
                        Key.MoveHome -> coroutine_scope.launch {
                            state.animateScrollToItem(0)
                        }
                        Key.MoveEnd -> coroutine_scope.launch {
                            state.animateScrollToItem(Int.MAX_VALUE)
                        }
                        else -> return@onKeyEvent false
                    }
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
            .focusRequester(focus_requester)
            .focusable()
            .padding(contentPadding.horizontal),
        horizontalArrangement = Arrangement.aligned(horizontalAlignment),
        verticalAlignment = verticalAlignment
    ) {
        val vertical_padding: PaddingValues = contentPadding.vertical
        val scrollbar_style: ScrollbarStyle = LocalScrollbarStyle.current.run {
            if (scrollBarColour.isUnspecified) this
            else copy(
                hoverColor = scrollBarColour,
                unhoverColor = scrollBarColour.copy(alpha = scrollBarColour.alpha * 0.25f)
            )
        }

        val scrollbar_adapter: ScrollbarAdapter = rememberScrollbarAdapter(state)
        val scrollbar_modifier: Modifier =
            Modifier
                .padding(vertical_padding)
                .height(with (density) { height.toDp() })

        if (reverseScrollBarLayout && show_scrollbar) {
            VerticalScrollbar(
                scrollbar_adapter,
                scrollbar_modifier,
                style = scrollbar_style
            )
        }

        LazyColumn(
            Modifier.weight(1f, false),
            state,
            vertical_padding,
            reverseLayout,
            verticalArrangement,
            horizontalAlignment,
            flingBehavior,
            false,
            content
        )

        if (!reverseScrollBarLayout && show_scrollbar) {
            VerticalScrollbar(
                scrollbar_adapter,
                scrollbar_modifier,
                style = scrollbar_style
            )
        }
    }
}
