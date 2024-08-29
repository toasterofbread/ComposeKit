package dev.toastbits.composekit.utils.composable.wave

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import dev.toastbits.composekit.utils.common.toInt
import dev.toastbits.composekit.utils.composable.pauseableInfiniteRepeatableAnimation
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.sin

@Composable
fun WaveLineArea(
    lineColour: Color,
    modifier: Modifier = Modifier,
    wavelength: Dp = 70.dp,
    periodMillis: Int = 2000,
    waveHeight: Dp = 15.dp,
    waveSpacing: Dp = 25.dp,
    waveThickness: Dp = 2.dp,
    rotationDegrees: Float = -15f,
    getStagger: (Int, Float) -> Float = { wave, offset -> (wave % 2 == 0).toInt() * offset },
    getPlaying: () -> Boolean = { true },
    content: @Composable () -> Unit = {}
) {
    val waveOffset: Float by
        pauseableInfiniteRepeatableAnimation(
            start = 0f,
            end = 1f,
            period = periodMillis,
            getPlaying = getPlaying
        )

    Box(modifier.clipToBounds()) {
        Canvas(Modifier.fillMaxSize()) {
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
                        getOffset = { (waveOffset + offset) % 1f }
                    )
                    path.translate(Offset(0f, position))
                    drawPath(path, lineColour, style = waveStroke)
                }
            }

            rotate(rotationDegrees) {
                for (wave in 0 until (maxOf(size.width, size.height) / waveSpacing.toPx()).toInt() * 2) {
                    drawWave((waveSpacing * wave).toPx(), getStagger(wave, waveOffset))
                }
            }
        }

        content()
    }
}

private inline fun DrawScope.wavePath(
    path: Path,
    direction: Int,
    height: Float,
    wavelength: Dp,
    outerRotationDegrees: Float = 0f,
    getOffset: () -> Float = { 0f }
): Path {
    path.reset()

    val halfPeriod: Float = wavelength.toPx() / 2

    val rotationAdj: Float = sin(outerRotationDegrees.toRadians())
    val maxSize: Float = maxOf(size.width, size.height)

    val effectiveWidth: Float = ceil(maxSize / halfPeriod) * halfPeriod

    val yOffset: Float = -(maxSize * rotationAdj * 0.5f)

    val xOffsetProgress: Float = getOffset()
    check(xOffsetProgress in 0f .. 1f) { xOffsetProgress }

    val xOffset: Float = xOffsetProgress * halfPeriod * 2
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
