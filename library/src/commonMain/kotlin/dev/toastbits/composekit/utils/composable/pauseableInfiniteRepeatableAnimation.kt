package dev.toastbits.composekit.utils.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

@Composable
fun pauseableInfiniteRepeatableAnimation(
    start: Float,
    end: Float,
    period: Int,
    playing: Boolean = true,
    initialOffsetMillis: Int = 0
): State<Float> {
    var animatable: Animatable<Float, AnimationVector1D> by remember { mutableStateOf(Animatable(start)) }
    var pausedAnimatablePosition: Int by remember { mutableStateOf(initialOffsetMillis) }

    LaunchedEffect(playing) {
        if (playing) {
            animatable.animateTo(
                end,
                infiniteRepeatable(
                    animation = tween(period, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(
                        pausedAnimatablePosition,
                        StartOffsetType.FastForward
                    )
                )
            )
        }
        else {
            pausedAnimatablePosition = ((animatable.value - start) / (end - start)).roundToInt() * period
            animatable = Animatable(0f)
        }
    }

    return animatable.asState()
}
