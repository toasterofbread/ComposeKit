package dev.toastbits.composekit.utils.composable.pane

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.settings.ui.ThemeValues
import dev.toastbits.composekit.settings.ui.vibrant_accent
import dev.toastbits.composekit.utils.common.copy
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParams
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParamsData
import dev.toastbits.composekit.utils.composable.pane.model.resizeAnimationSpecOrDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ResizableTwoPaneRow(
    startPaneContent: @Composable (PaddingValues) -> Unit,
    endPaneContent: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    showEndPane: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(),
    initialStartPaneRatio: Float = 0.5f,
    params: ResizablePaneContainerParams = ResizablePaneContainerParamsData()
) {
    val density: Density = LocalDensity.current
    val theme: ThemeValues = LocalApplicationTheme.current
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    var startPaneRatio: Float by remember { mutableFloatStateOf(initialStartPaneRatio) }

    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val hoveringOverDragHandle: Boolean by interactionSource.collectIsHoveredAsState()
    val draggingTimeout: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }

    BoxWithConstraints(modifier) {
        val dragHandleHovering: Boolean =
            hoveringOverDragHandle || draggingTimeout.isRunning
        val dragHandleWidth: Dp by animateDpAsState(if (dragHandleHovering) params.hoverDragHandleSize else params.dragHandleSize)
        val dragHandlePadding: Dp by animateDpAsState(if (dragHandleHovering) params.hoverDragHandlePadding else params.dragHandlePadding)

        val totalHandleWidth: Dp = dragHandleWidth + dragHandlePadding + dragHandlePadding
        val availableWidth: Dp = maxWidth - totalHandleWidth

        Row(Modifier.matchParentSize()) {
            var animating: Boolean by remember { mutableStateOf(false) }
            val startPaneWidth: Dp by animateDpAsState(
                if (showEndPane) availableWidth * startPaneRatio else this@BoxWithConstraints.maxWidth,
                animationSpec = params.resizeAnimationSpecOrDefault,
                finishedListener = {
                    animating = false
                }
            )

            LaunchedEffect(showEndPane) {
                animating = true
            }

            Box(
                Modifier
                    .width(
                        if (animating || params.resizeAnimationSpec != null) startPaneWidth
                        else availableWidth * startPaneRatio
                    )
                    .background(theme.background)
            ) {
                startPaneContent(contentPadding.copy(end = 0.dp))
            }

            AnimatedVisibility(
                showEndPane,
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(1f),
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Row {
                    if (totalHandleWidth > 0.dp) {
                        PaneResizeDragHandle(
                            Orientation.Horizontal,
                            theme.vibrant_accent,
                            Modifier
                                .fillMaxHeight()
                                .width(totalHandleWidth)
                                .hoverable(interactionSource, enabled = params.hoverable)
                                .padding(horizontal = dragHandlePadding)
                                .background(theme.card, RoundedCornerShape(10.dp)),
                            state = rememberDraggableState {
                                coroutineScope.launch {
                                    draggingTimeout.snapTo(1f)
                                    draggingTimeout.animateTo(0f, tween(params.dragTimeout.inWholeMilliseconds.toInt()))
                                }

                                with (density) {
                                    val deltaRatio: Float = it.toDp() / availableWidth
                                    val startPaneRatioRange: Float = params.minPaneWidth / availableWidth

                                    startPaneRatio = (startPaneRatio + deltaRatio).coerceIn(startPaneRatioRange .. (1f - startPaneRatioRange))
                                }
                            },
                            highlightSize = params.handleHighlightSize,
                            highlightShape = params.handleHighlightShape
                        )
                    }

                    Box(
                        Modifier
                            .width(availableWidth * (1f - startPaneRatio))
                            .background(theme.background)
                    ) {
                        endPaneContent(contentPadding.copy(start = 0.dp))
                    }
                }
            }
        }
    }
}
