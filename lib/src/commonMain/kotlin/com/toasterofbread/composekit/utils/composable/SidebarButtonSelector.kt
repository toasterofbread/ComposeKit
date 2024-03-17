package com.toasterofbread.composekit.utils.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.toasterofbread.composekit.utils.common.*
import kotlin.math.roundToInt

@Composable
fun <T> SidebarButtonSelector(
    selected_button: T,
    buttons: List<T>,
    indicator_colour: Color,
    onButtonSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    bottom_padding: Dp = 0.dp,
    scrolling: Boolean = true,
    vertical_arrangement: Arrangement.Vertical = Arrangement.Top,
    showButton: @Composable (T) -> Boolean = { true },
    isSpacing: (T) -> Boolean = { false },
    extraContent: @Composable ColumnScope.(T) -> Unit = {},
    buttonContent: @Composable (T) -> Unit
) {
    val button_positions: MutableMap<T, Float> = remember { mutableStateMapOf() }

    val button_indicator_alpha: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }
    val button_indicator_position: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }

    var previous_button: T? by remember { mutableStateOf(null) }
    var running: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(selected_button) {
        val button_position: Float? = button_positions[selected_button]
        if (button_position == null) {
            button_indicator_alpha.animateTo(0f)
            previous_button = null
            running = false
            return@LaunchedEffect
        }

        running = true

        if (previous_button == null) {
            button_indicator_position.snapTo(button_position)
            button_indicator_alpha.animateTo(1f)
        }
        else {
            var jump: Boolean = false

            var in_range: Boolean = false
            for (button in buttons) {
                if (button == selected_button || button == previous_button) {
                    if (in_range) {
                        break
                    }
                    in_range = true
                }
                else if (in_range && isSpacing(button)) {
                    jump = true
                    break
                }
            }

            if (jump) {
                button_indicator_alpha.animateTo(0f)
                button_indicator_position.snapTo(button_position)
                button_indicator_alpha.animateTo(1f)
            }
            else {
                button_indicator_position.animateTo(button_position)
            }
        }

        previous_button = selected_button

        running = false
    }

    BoxWithConstraints(
        modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            Modifier.thenIf(scrolling) {
                verticalScroll(rememberScrollState())
            }
        ) {
            CurrentButtonIndicator(
                indicator_colour,
                Modifier
                    .offset {
                        IntOffset(
                            0,
                            button_indicator_position.value.roundToInt()
                        )
                    }
                    .graphicsLayer {
                        alpha = if (!running) 0f else button_indicator_alpha.value
                    }
            )

            Column(
                Modifier.thenIf(scrolling) {
                    heightIn(min = this@BoxWithConstraints.maxHeight)
                },
                verticalArrangement = vertical_arrangement
            ) {
                for (button in buttons) {
                    extraContent(button)

                    AnimatedVisibility(
                        showButton(button),
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .onGloballyPositioned {
                                button_positions[button] = it.positionInParent().y
                            }
                    ) {
                        CurrentButtonIndicator(
                            indicator_colour,
                            Modifier
                                .graphicsLayer { alpha = (button == previous_button && !running).toFloat() }
                        )

                        ShapedIconButton(
                            { onButtonSelected(button) }
                        ) {
                            buttonContent(button)
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
        modifier
            .background(
                colour,
                CircleShape
            )
            .requiredSize(50.dp)
    )
}
