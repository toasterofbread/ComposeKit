package dev.toastbits.composekit.utils.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalDensity
import dev.toastbits.composekit.utils.common.*
import dev.toastbits.composekit.utils.common.thenWith
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope

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

    BoxWithOptionalConstraints(
        scrolling,
        modifier,
        contentAlignment = Alignment.Center
    ) { constraints ->
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
                Modifier.thenWith(constraints) {
                    if (vertical) heightIn(min = it.maxHeight)
                    else widthIn(min = it.maxWidth)
                },
                alignment = alignment,
                arrangement = arrangement
            ) {
                for ((index, button) in buttons.withIndex()) {
                    StickyLengthRowOrColumn(
                        !vertical,
                        key = buttons,
                        on_axis_alignment = 1
                    ) {
                        extraContent(index, button)
                    }

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
