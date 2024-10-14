package dev.toastbits.composekit.utils.composable

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.copy
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastSumBy
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs

// The built-in implementation of the animateScrollToItem doesn't work when using scrollOffset.
// I copy-pasted this implementation with the intent to fix it, but other than a slight change to
// isItemVisible, it works out of the box for some reason.

suspend fun LazyListState.workingAnimateScrollToItem(
    index: Int,
    scrollOffset: Int,
    density: Density
) {
    scroll {
        animateScrollToItem(index, scrollOffset, 100, density, this)
    }
}

/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

private suspend fun LazyListState.animateScrollToItem(
    index: Int,
    scrollOffset: Int,
    numOfItemsForTeleport: Int,
    density: Density,
    scrollScope: ScrollScope
) {
    check(index >= 0f) { "Index should be non-negative" }

    with(scrollScope) {
        try {
            val targetDistancePx = with(density) { TargetDistance.toPx() }
            val boundDistancePx = with(density) { BoundDistance.toPx() }
            val minDistancePx = with(density) { MinimumDistance.toPx() }
            var loop = true
            var anim = AnimationState(0f)

            if (isItemVisible(index)) {
                val targetItemInitialOffset = calculateDistanceTo(index, 0)
                // It's already visible, just animate directly
                throw ItemFoundInScroll(targetItemInitialOffset, anim)
            }
            val forward = index > firstVisibleItemIndex

            fun isOvershot(): Boolean {
                // Did we scroll past the item?
                @Suppress("RedundantIf") // It's way easier to understand the logic this way
                return if (forward) {
                    if (firstVisibleItemIndex > index) {
                        true
                    } else if (
                        firstVisibleItemIndex == index &&
                        firstVisibleItemScrollOffset > scrollOffset
                    ) {
                        true
                    } else {
                        false
                    }
                } else { // backward
                    if (firstVisibleItemIndex < index) {
                        true
                    } else if (
                        firstVisibleItemIndex == index &&
                        firstVisibleItemScrollOffset < scrollOffset
                    ) {
                        true
                    } else {
                        false
                    }
                }
            }

            var loops = 1
            while (loop && layoutInfo.totalItemsCount > 0) {
                val expectedDistance = calculateDistanceTo(index, 0) + scrollOffset
                val target =
                    if (abs(expectedDistance) < targetDistancePx) {
                        val absTargetPx = maxOf(abs(expectedDistance.toFloat()), minDistancePx)
                        if (forward) absTargetPx else -absTargetPx
                    } else {
                        if (forward) targetDistancePx else -targetDistancePx
                    }

                anim = anim.copy(value = 0f)
                var prevValue = 0f
                anim.animateTo(target, sequentialAnimation = (anim.velocity != 0f)) {
                    // If we haven't found the item yet, check if it's visible.
                    if (!isItemVisible(index)) {
                        // Springs can overshoot their target, clamp to the desired range
                        val coercedValue =
                            if (target > 0) {
                                value.coerceAtMost(target)
                            } else {
                                value.coerceAtLeast(target)
                            }
                        val delta = coercedValue - prevValue

                        val consumed = scrollBy(delta)
                        if (isItemVisible(index)) {
                        } else if (!isOvershot()) {
                            if (delta != consumed) {
                                cancelAnimation()
                                loop = false
                                return@animateTo
                            }
                            prevValue += delta
                            if (forward) {
                                if (value > boundDistancePx) {
                                    cancelAnimation()
                                }
                            } else {
                                if (value < -boundDistancePx) {
                                    cancelAnimation()
                                }
                            }

                            if (forward) {
                                if (
                                    loops >= 2 &&
                                    index - layoutInfo.visibleItemsInfo.last().index > numOfItemsForTeleport
                                ) {
                                    // Teleport
                                    snapToItem(index = index - numOfItemsForTeleport, offset = 0)
                                }
                            } else {
                                if (
                                    loops >= 2 &&
                                    firstVisibleItemIndex - index > numOfItemsForTeleport
                                ) {
                                    // Teleport
                                    snapToItem(index = index + numOfItemsForTeleport, offset = 0)
                                }
                            }
                        }
                    }

                    // We don't throw ItemFoundInScroll when we snap, because once we've snapped to
                    // the final position, there's no need to animate to it.
                    if (isOvershot()) {
                        snapToItem(index = index, offset = scrollOffset)
                        loop = false
                        cancelAnimation()
                        return@animateTo
                    } else if (isItemVisible(index)) {
                        val targetItemOffset = calculateDistanceTo(index, 0)
                        throw ItemFoundInScroll(targetItemOffset, anim)
                    }
                }

                loops++
            }
        } catch (itemFound: ItemFoundInScroll) {
            // We found it, animate to it
            // Bring to the requested position - will be automatically stopped if not possible
            val anim = itemFound.previousAnimation.copy(value = 0f)
            val target = (itemFound.itemOffset + scrollOffset).toFloat()
            var prevValue = 0f
            anim.animateTo(target, sequentialAnimation = (anim.velocity != 0f)) {
                // Springs can overshoot their target, clamp to the desired range
                val coercedValue =
                    when {
                        target > 0 -> {
                            value.coerceAtMost(target)
                        }
                        target < 0 -> {
                            value.coerceAtLeast(target)
                        }
                        else -> {
                            0f
                        }
                    }
                val delta = coercedValue - prevValue
                val consumed = scrollBy(delta)
                if (
                    delta != consumed /* hit the end, stop */ ||
                    coercedValue != value /* would have overshot, stop */
                ) {
                    cancelAnimation()
                }
                prevValue += delta
            }
            // Once we're finished the animation, snap to the exact position to account for
            // rounding error (otherwise we tend to end up with the previous item scrolled the
            // tiniest bit onscreen)
            // TODO: prevent temporarily scrolling *past* the item
            snapToItem(index = index, offset = scrollOffset)
        }
    }
}

private val TargetDistance = 2500.dp
private val BoundDistance = 1500.dp
private val MinimumDistance = 50.dp

private fun LazyListState.isItemVisible(index: Int): Boolean {
    return layoutInfo.visibleItemsInfo.any { it.index == index }
}

private class ItemFoundInScroll(
    val itemOffset: Int,
    val previousAnimation: AnimationState<Float, AnimationVector1D>
) : CancellationException()

private fun calculateVisibleItemsAverageSize(layoutInfo: LazyListLayoutInfo): Int {
    val visibleItems = layoutInfo.visibleItemsInfo
    val itemsSum = visibleItems.fastSumBy { it.size }
    return itemsSum / visibleItems.size + layoutInfo.mainAxisItemSpacing
}

private fun LazyListState.calculateDistanceTo(targetIndex: Int, targetOffset: Int): Int {
    if (layoutInfo.visibleItemsInfo.isEmpty()) return 0
    val visibleItem =
        layoutInfo.visibleItemsInfo.fastFirstOrNull { it.index == targetIndex }
    return if (visibleItem == null) {
        val averageSize = calculateVisibleItemsAverageSize(layoutInfo)
        val indexesDiff = targetIndex - firstVisibleItemIndex
        (averageSize * indexesDiff) - firstVisibleItemScrollOffset
    } else {
        visibleItem.offset
    } + targetOffset
}

private fun LazyListState.snapToItem(index: Int, offset: Int) {
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
    snapToItemIndexInternal(index, offset, forceRemeasure = true)
}
