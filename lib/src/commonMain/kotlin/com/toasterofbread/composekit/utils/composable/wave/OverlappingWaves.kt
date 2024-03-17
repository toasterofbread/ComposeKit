package com.toasterofbread.composekit.utils.composable.wave

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

fun getDefaultOverlappingWavesLayers(
    time_period_multiplier: Long = 1,
    height_multiplier: Float = 1f,
    alpha_multiplier: Float = 1f
): List<WaveLayer> =
    listOf(
        WaveLayer(
            time_period = time_period_multiplier * 300,
            start_time = 0,
            height = height_multiplier * 0.7f,
            base = height_multiplier * 0.2f,
            wavelength = 800.dp,
            alpha = alpha_multiplier * 0.5f
        ),
        WaveLayer(
            time_period = time_period_multiplier * 500,
            start_time = 200,
            height = height_multiplier * 0.7f,
            base = height_multiplier * 0.1f,
            wavelength = 650.dp,
            alpha = alpha_multiplier * 0.8f
        ),
        WaveLayer(
            time_period = time_period_multiplier * 650,
            start_time = 700,
            height = 0.5f,
            base = 0.0f,
            wavelength = 1600.dp,
            alpha = alpha_multiplier * 0.5f
        ),
        WaveLayer(
            time_period = time_period_multiplier * 700,
            start_time = 0,
            height = 0.7f,
            base = 0.2f,
            wavelength = 1200.dp,
            alpha = alpha_multiplier * 0.5f
        ),
        WaveLayer(
            time_period = time_period_multiplier * 350,
            start_time = 700,
            height = 0.6f,
            base = 0.1f,
            wavelength = 1000.dp,
            alpha = alpha_multiplier * 0.5f
        ),
    )

@Composable
fun OverlappingWaves(
    getColour: () -> Color,
    blend_mode: BlendMode,
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    layers: List<WaveLayer> = remember { getDefaultOverlappingWavesLayers() },
    getLayerExtraHeight: Density.(layer: Int) -> Float = { 0f }
) {
    val density: Density = LocalDensity.current
    var canvas_width: Dp by remember { mutableStateOf(1.dp) }
    
    LaunchedEffect(layers, canvas_width, speed) {
        withContext(Dispatchers.Default) {
            val delta: Long = 10
            while (true) {
                for (layer in layers) {
                    layer.progressTime((delta * speed).roundToLong(), canvas_width)
                }
                delay(delta)
            }
        }
    }

    Canvas(modifier.onSizeChanged { canvas_width = with (density) { it.width.toDp() } }) {
        val colour: Color = getColour()
        for ((i, layer) in layers.withIndex()) {
            with (layer) {
                draw(colour, blend_mode, getLayerExtraHeight(i))
            }
        }
    }
}
