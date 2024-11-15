package dev.toastbits.composekit.utils.composable.pane.model

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import kotlin.time.Duration

interface ResizablePaneContainerParams: ResizablePaneContainerParamsProvider {
    val dragHandleSize: Dp
    val dragHandlePadding: Dp
    val hoverDragHandleSize: Dp
    val hoverDragHandlePadding: Dp
    val hoverable: Boolean
    val handleHighlightSize: Dp
    val handleHighlightShape: Shape
    val minPaneWidth: Dp
    val dragTimeout: Duration
    val resizeAnimationSpec: AnimationSpec<Dp>?

    @Composable
    override operator fun invoke(): ResizablePaneContainerParams = this

    companion object {
        val DEFAULT_RESIZE_ANIMATION_SPEC: SpringSpec<Dp> = spring(visibilityThreshold = Dp.VisibilityThreshold)
    }
}

val ResizablePaneContainerParams.resizeAnimationSpecOrDefault: AnimationSpec<Dp>
    get() = resizeAnimationSpec ?: ResizablePaneContainerParams.DEFAULT_RESIZE_ANIMATION_SPEC
