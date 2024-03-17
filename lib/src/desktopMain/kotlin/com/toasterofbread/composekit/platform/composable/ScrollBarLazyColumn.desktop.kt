package com.toasterofbread.composekit.platform.composable

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.toasterofbread.composekit.utils.modifier.horizontal
import com.toasterofbread.composekit.utils.modifier.vertical
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
            .scrollWheelScrollable(state)
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
            userScrollEnabled,
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
