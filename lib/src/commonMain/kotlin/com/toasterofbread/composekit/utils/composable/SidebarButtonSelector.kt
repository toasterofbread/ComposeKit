package com.toasterofbread.composekit.utils.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.toasterofbread.composekit.utils.common.thenIf
import com.toasterofbread.composekit.utils.common.toFloat
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
                        Modifier.onGloballyPositioned {
                            button_positions[button] = it.positionInParent().y
                        }
                    ) {
                        Box(Modifier.requiredSize(0.dp)) {
                            CurrentButtonIndicator(
                                indicator_colour,
                                Modifier
                                    .offset(25.dp, 25.dp)
                                    .graphicsLayer { alpha = (button == previous_button && !running).toFloat() }
                            )
                        }

                        IconButton(
                            {
                                onButtonSelected(button)
                            }
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
