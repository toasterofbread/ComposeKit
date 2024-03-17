package com.toasterofbread.composekit.utils.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.toasterofbread.composekit.utils.common.thenIf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> SidebarButtonSelector(
    selected_button: Int?,
    buttons: List<T>,
    indicator_colour: Color,
    modifier: Modifier = Modifier,
    bottom_padding: Dp = 0.dp,
    scrolling: Boolean = true,
    vertical: Boolean = true,
    alignment: Int = -1,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(0.dp),
    showButton: @Composable (T) -> Boolean = { true },
    isSpacing: (T) -> Boolean = { false },
    extraContent: @Composable RowOrColumnScope.(Int, T) -> Unit = { _, _ -> },
    getButtonModifier: @Composable RowOrColumnScope.(Int, T) -> Modifier = { _, _ -> Modifier },
    buttonContent: @Composable (Int, T) -> Unit
) {
    val density: Density = LocalDensity.current

    val button_positions: MutableMap<Int, Float> = remember { mutableStateMapOf() }
    val button_sizes: MutableMap<Int, DpSize> = remember { mutableStateMapOf() }

    val button_indicator_alpha: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }
    val button_indicator_position: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }
    val button_indicator_width: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }
    val button_indicator_height: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }

    var previous_button: Int? by remember { mutableStateOf(null) }
    var running: Boolean by remember { mutableStateOf(false) }
    var target_button: Int? by remember { mutableStateOf(null) }

    LaunchedEffect(selected_button) {
        val button_position: Float? = selected_button?.let { button_positions[it] }
        val button_size: DpSize? = selected_button?.let { button_sizes[it] }

        if (button_position == null || button_size == null) {
            button_indicator_alpha.animateTo(0f)
            previous_button = null
            running = false
            target_button = selected_button
            return@LaunchedEffect
        }

        running = true

        if (previous_button == null) {
            coroutineScope {
                launch {
                    button_indicator_position.snapTo(button_position)
                }
                launch {
                    button_indicator_width.snapTo(button_size.width.value)
                }
                launch {
                    button_indicator_height.snapTo(button_size.height.value)
                }
                target_button = selected_button
            }
            button_indicator_alpha.animateTo(1f)
        }
        else {
            var snap: Boolean = false

            var in_range: Boolean = false
            for ((index, button) in buttons.withIndex()) {
                if (index == selected_button || index == previous_button) {
                    if (in_range) {
                        break
                    }
                    in_range = true
                }
                else if (in_range && isSpacing(button)) {
                    snap = true
                    break
                }
            }

            if (snap) {
                button_indicator_alpha.animateTo(0f)
                coroutineScope {
                    launch {
                        button_indicator_position.snapTo(button_position)
                    }
                    launch {
                        button_indicator_width.snapTo(button_size.width.value)
                    }
                    launch {
                        button_indicator_height.snapTo(button_size.height.value)
                    }
                    target_button = selected_button
                }
                button_indicator_alpha.animateTo(1f)
            }
            else {
                coroutineScope {
                    launch {
                        button_indicator_position.animateTo(button_position)
                    }
                    launch {
                        button_indicator_width.snapTo(button_size.width.value)
                    }
                    launch {
                        button_indicator_height.snapTo(button_size.height.value)
                    }
                    target_button = selected_button
                }
            }
        }

        previous_button = selected_button

        running = false
    }

    BoxWithConstraints(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier.thenIf(scrolling) {
                if (vertical) verticalScroll(rememberScrollState())
                else horizontalScroll(rememberScrollState())
            }
        ) {
            CurrentButtonIndicator(
                indicator_colour,
                Modifier
                    .offset {
                        val position: Int = button_indicator_position.value.roundToInt()
                        if (vertical) IntOffset(0, position)
                        else IntOffset(position, 0)
                    }
                    .graphicsLayer {
                        alpha = if (!running) 0f else button_indicator_alpha.value
                    }
                    .size(button_indicator_width.value.dp, button_indicator_height.value.dp)
            )

            RowOrColumn(
                !vertical,
                Modifier.thenIf(scrolling) {
                    if (vertical) heightIn(min = this@BoxWithConstraints.maxHeight)
                    else widthIn(min = this@BoxWithConstraints.maxWidth)
                },
                alignment = alignment,
                arrangement = arrangement
            ) {
                for ((index, button) in buttons.withIndex()) {
                    extraContent(index, button)

                    AnimatedVisibility(
                        showButton(button),
                        getButtonModifier(index, button)
                            .sizeIn(minWidth = 50.dp, minHeight = 50.dp)
                            .width(IntrinsicSize.Min)
                            .height(IntrinsicSize.Min)
                            .onGloballyPositioned {
                                button_positions[index] =
                                    if (vertical) it.positionInParent().y
                                    else it.positionInParent().x
                            }
                            .onSizeChanged {
                                button_sizes[index] = with (density) {
                                    DpSize(it.width.toDp(), it.height.toDp())
                                }
                            },
                        enter =
                            if (vertical) expandVertically()
                            else expandHorizontally(),
                        exit =
                            if (vertical) shrinkVertically()
                            else shrinkHorizontally()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (index == target_button && !running) {
                                CurrentButtonIndicator(
                                    indicator_colour,
                                    Modifier.fillMaxSize()
                                )
                            }

                            buttonContent(index, button)
                        }
                    }
                }

                Spacer(Modifier.height(bottom_padding))
            }
        }
    }
}

@Composable
private fun CurrentButtonIndicator(colour: Color, modifier: Modifier = Modifier) {
    Box(
        modifier.background(
            colour,
            CircleShape
        )
    )
}
