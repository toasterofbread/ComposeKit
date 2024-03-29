@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package com.toasterofbread.composekit.utils.modifier

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollingLayoutNode
import androidx.compose.foundation.checkScrollableContainerConstraints
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.overscroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.horizontalScrollAxisRange
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.scrollBy
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.verticalScrollAxisRange
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.launch

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
