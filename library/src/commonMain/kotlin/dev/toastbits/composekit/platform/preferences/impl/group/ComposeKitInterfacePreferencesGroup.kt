package dev.toastbits.composekit.platform.preferences.impl.group

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.preferences.PreferencesGroup
import dev.toastbits.composekit.platform.preferences.PreferencesProperty
import dev.toastbits.composekit.utils.common.thenIf
import dev.toastbits.composekit.utils.common.toFloat
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParams
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParamsData
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParamsProvider

interface ComposeKitInterfacePreferencesGroup: PreferencesGroup {
    val SHOW_PANE_RESIZE_HANDLES: PreferencesProperty<Boolean>
    val SHOW_PANE_RESIZE_HANDLES_ON_HOVER: PreferencesProperty<Boolean>
    val ANIMATE_PANE_RESIZE: PreferencesProperty<Boolean>
}

@Composable
fun ComposeKitInterfacePreferencesGroup.getResizablePaneContainerParams(
    default: ResizablePaneContainerParams = ResizablePaneContainerParamsData()
): ResizablePaneContainerParams {
    val showPaneResizeHandles: Boolean by SHOW_PANE_RESIZE_HANDLES.observe()
    val showPaneResizeHandlesOnHover: Boolean by SHOW_PANE_RESIZE_HANDLES_ON_HOVER.observe()
    val animatePaneResize: Boolean by ANIMATE_PANE_RESIZE.observe()

    val dragHandleWidth: Float by animateFloatAsState(showPaneResizeHandles.toFloat())

    return ResizablePaneContainerParamsData(
        dragHandleWidth = default.dragHandleWidth * dragHandleWidth,
        dragHandlePadding =
            (default.dragHandlePadding * dragHandleWidth).thenIf(showPaneResizeHandlesOnHover) { coerceAtLeast(10.dp) },
        hoverDragHandleWidth =
            if (showPaneResizeHandles || showPaneResizeHandlesOnHover) default.dragHandleWidth
            else default.dragHandleWidth * dragHandleWidth,
        hoverDragHandlePadding =
            if (showPaneResizeHandles || showPaneResizeHandlesOnHover) default.dragHandlePadding
            else default.dragHandlePadding * dragHandleWidth,
        resizeAnimationSpec =
            if (animatePaneResize) ResizablePaneContainerParams.DEFAULT_RESIZE_ANIMATION_SPEC
            else null
    )
}
