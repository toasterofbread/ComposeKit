package dev.toastbits.composekit.utils.composable.pane

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.utils.common.launchSingle
import dev.toastbits.composekit.utils.modifier.background
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun PaneResizeDragHandle(
    orientation: Orientation,
    state: DraggableState,
    highlightColour: Color,
    modifier: Modifier = Modifier,
    onDraggingChanged: (Boolean) -> Unit = {},
    highlightSize: Dp = 50.dp,
    highlightShape: Shape = RoundedCornerShape(10.dp),
    dragTimeout: Duration = 100.milliseconds,
) {
    var dragging: Boolean by remember { mutableStateOf(false) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val hoverInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val hovering: Boolean by hoverInteractionSource.collectIsHoveredAsState()

    val highlightOpacity: Float by animateFloatAsState(if (hovering || dragging) 1f else 0.25f)

    LaunchedEffect(dragging) {
        onDraggingChanged(dragging)
    }

    Box(
        modifier
            .draggable(
                orientation = orientation,
                state = rememberDraggableState {
                    state.dispatchRawDelta(it)
                    coroutineScope.launchSingle {
                        dragging = true
                        delay(dragTimeout)
                        dragging = false
                    }
                }
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .hoverable(hoverInteractionSource),
        contentAlignment = Alignment.Center
    ) {
        Box(
            when (orientation) {
                Orientation.Vertical ->
                    Modifier.width(highlightSize).fillMaxHeight()
                Orientation.Horizontal ->
                    Modifier.height(highlightSize).fillMaxWidth()
            }
        ) {
            Box(
                Modifier
                    .matchParentSize()
                    .run {
                        when (orientation) {
                            Orientation.Vertical -> padding(vertical = 3.dp)
                            Orientation.Horizontal -> padding(horizontal = 3.dp)
                        }
                    }
                    .background(highlightShape) { highlightColour.copy(alpha = highlightOpacity) }
            )
        }
    }
}
