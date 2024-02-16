package com.toasterofbread.composekit.utils.composable.wave

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import com.toasterofbread.composekit.utils.common.amplifyPercent
import kotlin.math.ceil

fun Float.decimalPart(): Float =
    this - this.toInt()

fun DrawScope.drawRepeatableWave(
    brush: Brush,
    visible_area_size: Size,
    wavelength: Dp,
    direction: Int = 1,
    offset: Float = 0f,
    base_height: Float = 0f,
    blend_mode: BlendMode = DrawScope.DefaultBlendMode
) {
    val wavelengths_in_width: Float = visible_area_size.width / wavelength.toPx()

    var actual_waves: Int = (wavelengths_in_width * 4).toInt()
    if (actual_waves < 2) {
        actual_waves = 2
    }
    else if (actual_waves % 2 != 0) {
        actual_waves++
    }

    val startpoint_offset: Float = wavelengths_in_width.decimalPart() * wavelength.toPx()
    val minimum_width: Float = (actual_waves.toFloat() / 2f) * wavelength.toPx()

    val travel_distance = minimum_width - visible_area_size.width + startpoint_offset
    val offset_px: Float = ((offset - 1f) * travel_distance)

    val draw_waves: Int = actual_waves + 2
    val draw_width: Float = (draw_waves / 2f) * wavelength.toPx()

    val half_height: Float = visible_area_size.height / 2f
    val quarter_period: Float = visible_area_size.width / (wavelengths_in_width * 2)

    val path: Path = Path()
    path.moveTo(x = offset_px, y = half_height)

    for (i in 0 until draw_waves) {
        path.relativeQuadraticBezierTo(
            dx1 = quarter_period / 2,
            dy1 = visible_area_size.height * (if (i % 2 == 0) 1 else -1),
            dx2 = quarter_period,
            dy2 = 0f
        )
    }

    path.relativeLineTo(0f, (half_height + base_height) * direction)
    path.relativeLineTo(-draw_width, 0f)
    path.relativeLineTo(0f, (half_height + base_height) * -direction)

    drawPath(path, brush, blendMode = blend_mode)
}

class WaveLayer(
    val time_period: Long,
    start_time: Long,
    val height: Float,
    val base: Float,
    val wavelength: Dp,
    val alpha: Float,
    val highlight_height: Float = 0.4f
) {
    init {
        require(height + base <= 1f)
    }

    fun DrawScope.draw(colour: Color, blend_mode: BlendMode, extra_height: Float = 0f) {
        translate(top = ((1f - height - base) * size.height) - extra_height) {
            drawRepeatableWave(
                brush = getBrush(colour.copy(alpha = colour.alpha * alpha)),
                visible_area_size = size.copy(height = (size.height * height)),
                wavelength = wavelength,
                offset = time_progress,
                blend_mode = blend_mode,
                base_height = (size.height * base) + extra_height
            )
        }
    }

    fun progressTime(by: Long, canvas_width: Dp) {
        val waves: Float = (canvas_width / wavelength) * 2

        var actual_waves: Int = ceil(waves * 2).toInt()
        if (actual_waves % 2 == 0) {
            actual_waves++
        }

        val total_period: Float = time_period * (actual_waves / 2f)
        
        time_progress = (time_progress + (by / total_period)) % 1f
        if (time_progress < 0f) {
            time_progress += 1 - time_progress.toInt()
        }
    }

    private var time_progress: Float by mutableStateOf(0f)

    private fun DrawScope.getBrush(colour: Color) =
        Brush.verticalGradient(
            listOf(
                colour.amplifyPercent(0.025f),
                colour
            ),
            endY = size.height * highlight_height
        )
}
