@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package com.toasterofbread.composekit.utils.modifier

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.checkScrollableContainerConstraints
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.overscroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.scrollWithoutClip(
    state: ScrollState,
    is_vertical: Boolean,
    reverse_scrolling: Boolean = false,
    fling_behaviour: FlingBehavior? = null,
    is_scrollable: Boolean = true
) = composed(
    factory = {
        val overscrollEffect = ScrollableDefaults.overscrollEffect()
        val coroutineScope = rememberCoroutineScope()
        val semantics = Modifier.semantics {
            isTraversalGroup = true
            val accessibilityScrollState = ScrollAxisRange(
                value = { state.value.toFloat() },
                maxValue = { state.maxValue.toFloat() },
                reverseScrolling = reverse_scrolling
            )
            if (is_vertical) {
                this.verticalScrollAxisRange = accessibilityScrollState
            } else {
                this.horizontalScrollAxisRange = accessibilityScrollState
            }
            if (is_scrollable) {
                // when b/156389287 is fixed, this should be proper scrollTo with reverse handling
                scrollBy(
                    action = { x: Float, y: Float ->
                        coroutineScope.launch {
                            if (is_vertical) {
                                (state as ScrollableState).animateScrollBy(y)
                            } else {
                                (state as ScrollableState).animateScrollBy(x)
                            }
                        }
                        return@scrollBy true
                    }
                )
            }
        }
        val orientation = if (is_vertical) Orientation.Vertical else Orientation.Horizontal
        val scrolling = Modifier.scrollable(
            orientation = orientation,
            reverseDirection = ScrollableDefaults.reverseDirection(
                LocalLayoutDirection.current,
                orientation,
                reverse_scrolling
            ),
            enabled = is_scrollable,
            interactionSource = state.internalInteractionSource,
            flingBehavior = fling_behaviour,
            state = state,
            overscrollEffect = overscrollEffect
        )
        val layout =
            ScrollingLayoutElement(state, reverse_scrolling, is_vertical)
        semantics
            //            .clipScrollableContainer(orientation)
            .overscroll(overscrollEffect)
            .then(scrolling)
            .then(layout)
              },
    inspectorInfo = debugInspectorInfo {
        name = "scroll"
        properties["state"] = state
        properties["reverseScrolling"] = reverse_scrolling
        properties["flingBehavior"] = fling_behaviour
        properties["isScrollable"] = is_scrollable
        properties["isVertical"] = is_vertical
    }
)

private class ScrollingLayoutElement(
    val scrollState: ScrollState,
    val isReversed: Boolean,
    val isVertical: Boolean
) : ModifierNodeElement<ScrollingLayoutNode>() {
    override fun create(): ScrollingLayoutNode {
        return ScrollingLayoutNode(
            scrollerState = scrollState,
            isReversed = isReversed,
            isVertical = isVertical
        )
    }

    override fun update(node: ScrollingLayoutNode) {
        node.scrollerState = scrollState
        node.isReversed = isReversed
        node.isVertical = isVertical
    }

    override fun hashCode(): Int {
        var result = scrollState.hashCode()
        result = 31 * result + isReversed.hashCode()
        result = 31 * result + isVertical.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ScrollingLayoutElement) return false
        return scrollState == other.scrollState &&
               isReversed == other.isReversed &&
               isVertical == other.isVertical
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "layoutInScroll"
        properties["state"] = scrollState
        properties["isReversed"] = isReversed
        properties["isVertical"] = isVertical
    }
}

private class ScrollingLayoutNode(
    var scrollerState: ScrollState,
    var isReversed: Boolean,
    var isVertical: Boolean
) : LayoutModifierNode, Modifier.Node() {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        checkScrollableContainerConstraints(
            constraints,
            if (isVertical) Orientation.Vertical else Orientation.Horizontal
        )

        val childConstraints = constraints.copy(
            maxHeight = if (isVertical) Constraints.Infinity else constraints.maxHeight,
            maxWidth = if (isVertical) constraints.maxWidth else Constraints.Infinity
        )
        val placeable = measurable.measure(childConstraints)
        val width = placeable.width.coerceAtMost(constraints.maxWidth)
        val height = placeable.height.coerceAtMost(constraints.maxHeight)
        val scrollHeight = placeable.height - height
        val scrollWidth = placeable.width - width
        val side = if (isVertical) scrollHeight else scrollWidth
        // The max value must be updated before returning from the measure block so that any other
        // chained RemeasurementModifiers that try to perform scrolling based on the new
        // measurements inside onRemeasured are able to scroll to the new max based on the newly-
        // measured size.
        //        scrollerState.maxValue = side
        scrollerState.viewportSize = if (isVertical) height else width
        return layout(width, height) {
            val scroll = scrollerState.value.coerceIn(0, side)
            val absScroll = if (isReversed) scroll - side else -scroll
            val xOffset = if (isVertical) 0 else absScroll
            val yOffset = if (isVertical) absScroll else 0
            placeable.placeRelativeWithLayer(xOffset, yOffset)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return if (isVertical) {
            measurable.minIntrinsicWidth(Constraints.Infinity)
        } else {
            measurable.minIntrinsicWidth(height)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return if (isVertical) {
            measurable.minIntrinsicHeight(width)
        } else {
            measurable.minIntrinsicHeight(Constraints.Infinity)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int {
        return if (isVertical) {
            measurable.maxIntrinsicWidth(Constraints.Infinity)
        } else {
            measurable.maxIntrinsicWidth(height)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int {
        return if (isVertical) {
            measurable.maxIntrinsicHeight(width)
        } else {
            measurable.maxIntrinsicHeight(Constraints.Infinity)
        }
    }
}
