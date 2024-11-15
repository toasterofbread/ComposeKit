package dev.toastbits.composekit.utils.composable.pane.model

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class ResizablePaneContainerParamsData(
    override val dragHandleWidth: Dp = 12.dp,
    override val dragHandlePadding: Dp = 12.dp,
    override val hoverDragHandleWidth: Dp = 12.dp,
    override val hoverDragHandlePadding: Dp = 12.dp,
    override val handleHighlightSize: Dp = 50.dp,
    override val handleHighlightShape: Shape = RoundedCornerShape(10.dp),
    override val minPaneWidth: Dp = 50.dp,
    override val dragTimeout: Duration = 100.milliseconds,
    override val resizeAnimationSpec: AnimationSpec<Dp>? =
        if (PLATFORM_DEFAULT_USE_PANE_RESIZE_ANIMATION_SPEC) ResizablePaneContainerParams.DEFAULT_RESIZE_ANIMATION_SPEC
        else null
): ResizablePaneContainerParams

expect val PLATFORM_DEFAULT_USE_PANE_RESIZE_ANIMATION_SPEC: Boolean
