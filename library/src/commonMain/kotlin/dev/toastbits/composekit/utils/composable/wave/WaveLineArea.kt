package dev.toastbits.composekit.utils.composable.wave

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.composable.theme.LocalApplicationTheme
import dev.toastbits.composekit.utils.common.toInt
import dev.toastbits.composekit.utils.composable.pauseableInfiniteRepeatableAnimation
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sin

val LocalWaveLineAreaState: ProvidableCompositionLocal<WaveLineAreaState?> =
    staticCompositionLocalOf { null }

typealias WaveLineAreaState = State<Float>

@Composable
fun WaveLineArea(
    modifier: Modifier = Modifier,
    lineColour: Color = LocalApplicationTheme.current.accent.copy(alpha = 0.2f),
    wavelength: Dp = 70.dp,
    periodMillis: Int = 3000,
    waveHeight: Dp = 15.dp,
    waveSpacing: Dp = 25.dp,
    waveThickness: Dp = 2.dp,
    rotationDegrees: Float = -15f,
    playing: Boolean = true,
    enabled: Boolean = true,
    initialOffset: Float = 0f,
    getStagger: (Int, Float) -> Float = { wave, offset -> (wave % 2 == 0).toInt() * offset },
    transformOffset: (Float) -> Float = { it },
    content: @Composable () -> Unit = {}
): State<Float> {
    require(initialOffset in 0f .. 1f)

    val waveState: WaveLineAreaState =
        LocalWaveLineAreaState.current
        ?: pauseableInfiniteRepeatableAnimation(
            start = 0f,
            end = 1f,
            period = periodMillis,
            playing = playing,
            initialOffsetMillis = (initialOffset * periodMillis).roundToInt()
        )

    Box(modifier.clipToBounds()) {
        Canvas(Modifier.fillMaxSize()) {
            if (!enabled) {
                return@Canvas
            }

            val path: Path = Path()
            val waveStroke: Stroke = Stroke(waveThickness.toPx())

            fun drawWave(position: Float, offset: Float) {
                for (direction in listOf(-1, 1)) {
                    wavePath(
                        path = path,
                        direction = direction,
                        height = waveHeight.toPx(),
                        wavelength = wavelength,
                        outerRotationDegrees = rotationDegrees,
                        offset = offset % 1f
                    )
                    path.translate(Offset(0f, position))
                    drawPath(path, lineColour, style = waveStroke)
                }
            }

            val offset: Float = transformOffset(waveState.value) % 1f

            rotate(rotationDegrees) {
                for (wave in 0 until (maxOf(size.width, size.height) / waveSpacing.toPx()).toInt() * 2) {
                    drawWave((waveSpacing * wave).toPx(), offset + getStagger(wave, offset))
                }
            }
        }

        content()
    }

    return waveState
}

private fun DrawScope.wavePath(
    path: Path,
    direction: Int,
    height: Float,
    wavelength: Dp,
    outerRotationDegrees: Float = 0f,
    offset: Float
): Path {
    path.reset()

    val halfPeriod: Float = wavelength.toPx() / 2

    val rotationAdj: Float = sin(outerRotationDegrees.toRadians())
    val maxSize: Float = maxOf(size.width, size.height)

    val effectiveWidth: Float = ceil(maxSize / halfPeriod) * halfPeriod

    val yOffset: Float = -(maxSize * rotationAdj * 0.5f)

    check(offset in 0f .. 1f) { offset }

    val xOffset: Float = offset * halfPeriod * 2
    val xAdjustedOffset = (xOffset % effectiveWidth) - (if (xOffset > 0f) effectiveWidth else 0f)
    path.moveTo(x = -halfPeriod / 2 + xAdjustedOffset, y = yOffset)

    for (i in 0 until ceil((effectiveWidth * 2) / halfPeriod + 1).toInt()) {
        if ((i % 2 == 0) != (direction == 1)) {
            path.relativeMoveTo(halfPeriod, 0f)
            continue
        }

        path.relativeQuadraticTo(
            dx1 = halfPeriod / 2,
            dy1 = height / 2 * direction,
            dx2 = halfPeriod,
            dy2 = 0f
        )
    }

    return path
}

private fun Float.toRadians(): Float =
    (this * 180f) / PI.toFloat()
