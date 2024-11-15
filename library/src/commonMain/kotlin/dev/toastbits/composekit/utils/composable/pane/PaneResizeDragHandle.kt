package dev.toastbits.composekit.utils.composable.pane

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun PaneResizeDragHandle(
    orientation: Orientation,
    highlightColour: Color,
    modifier: Modifier = Modifier,
    state: DraggableState,
    highlightSize: Dp = 50.dp,
    highlightShape: Shape = RoundedCornerShape(10.dp)
) {
    Box(
        modifier
            .draggable(
                orientation = orientation,
                state = state
            )
            .pointerHoverIcon(PointerIcon.Hand),
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
                    .background(highlightColour, highlightShape)
            )
        }
    }
}
