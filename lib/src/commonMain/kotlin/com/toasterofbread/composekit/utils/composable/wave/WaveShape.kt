package com.toasterofbread.composekit.utils.composable.wave

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.ceil

data class WaveShape(
    val waves: Int,
    val offset: Float,
    val invert: Boolean = false,
    val width_multiplier: Float = 1f
): Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path: Path = Path()

        path.addRect(Rect(0f, 0f, size.width, size.height / 2))

        wavePath(path, size, 1, waves, width_multiplier) { offset }
        wavePath(path, size, -1, waves, width_multiplier) { offset }

        if (invert) {
            path.transform(
                Matrix().apply {
                    scale(y = -1f)
                    translate(y = -size.height)
                }
            )
        }

        return Outline.Generic(path)
    }
}

inline fun DrawScope.drawWave(
    waves: Int,
    wave_size: Size = size,
    stroke_width: Float = 2f,
    width_multiplier: Float = 1f,
    getWaveOffset: () -> Float,
    getColour: () -> Color,
    ) {
    val path: Path = Path()
    val colour: Color = getColour()
    val stroke: Stroke = Stroke(stroke_width)

    // Above equilibrium
    wavePath(path, wave_size, -1, waves, width_multiplier, getWaveOffset)
    drawPath(path, colour, style = stroke)
    path.reset()

    // Below equilibrium
    wavePath(path, wave_size, 1, waves, width_multiplier, getWaveOffset)
    drawPath(path, colour, style = stroke)
}

inline fun wavePath(
    path: Path,
    size: Size,
    direction: Int,
    waves: Int,
    width_multiplier: Float,
    getOffset: () -> Float
): Path {
    val y_offset: Float = size.height / 2
    val half_period: Float = size.width / waves
    val offset_px: Float = getOffset().let { offset ->
        (offset % (size.width)) - (if (offset > 0f) size.width else 0f)
    }

    path.moveTo(x = offset_px, y = y_offset)

    for (i in 0 until ceil((size.width * width_multiplier) / (half_period + 1)).toInt()) {
        if ((i % 2 == 0) != (direction == 1)) {
            path.relativeMoveTo(half_period, 0f)
            continue
        }

        path.relativeQuadraticBezierTo(
            dx1 = half_period / 2,
            dy1 = size.height / 2 * direction,
            dx2 = half_period,
            dy2 = 0f,
            )
    }

    return path
}
