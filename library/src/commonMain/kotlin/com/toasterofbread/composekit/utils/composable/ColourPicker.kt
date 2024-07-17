package dev.toastbits.composekit.utils.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.godaddy.colorpicker.colorpicker.ClassicColorPicker
import com.godaddy.colorpicker.colorpicker.HsvColor
import dev.toastbits.composekit.utils.common.contrastAgainst
import dev.toastbits.composekit.utils.common.fromHexString
import dev.toastbits.composekit.utils.common.generatePalette
import dev.toastbits.composekit.utils.common.sorted

@Composable
fun ColourPicker(
    current_colour: Color,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    presets: List<Color>? = null,
    bottomRowExtraContent: (@Composable () -> Unit)? = null,
    onSelected: (Color) -> Unit
) {
    val density: Density = LocalDensity.current
    var instance: Boolean by remember { mutableStateOf(false) }

    val colour_presets: List<Color> = remember(current_colour, presets) {
        (presets ?: current_colour.generatePalette(10, 1f)).sorted(true)
    }

    OnChangedEffect(current_colour) {
        instance = !instance
    }

    Column(
        modifier,
        verticalArrangement = arrangement
    ) {
        Row(horizontalArrangement = arrangement) {
            var height: Dp by remember { mutableStateOf(0.dp) }

            LazyColumn(
                Modifier.height( height ),
                verticalArrangement = arrangement
            ) {
                items(colour_presets) { colour ->
                    colour.presetItem(current_colour) {
                        onSelected(colour)
                        instance = !instance
                    }
                }
            }

            Crossfade(instance) {
                ClassicColorPicker(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .aspectRatio(1f)
                        .onSizeChanged {
                            height = with (density) {
                                it.height.toDp()
                            }
                        },
                    HsvColor.from(current_colour),
                    showAlphaBar = false
                ) { colour ->
                    onSelected(colour.toColor())
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = arrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var text_field_content: String by remember { mutableStateOf("") }
            var text_field_error: Boolean by remember { mutableStateOf(false) }

            TextField(
                text_field_content,
                { text_field_content = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                singleLine = true,
                isError = text_field_error,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val colour: Color
                        try {
                            colour = Color.fromHexString(text_field_content)
                            text_field_error = false
                        }
                        catch (_: Throwable) {
                            text_field_error = true
                            return@KeyboardActions
                        }

                        onSelected(colour)
                    }
                )
            )

            bottomRowExtraContent?.invoke()
        }
    }
}

@Composable
private fun Color.presetItem(current_colour: Color, onSelected: () -> Unit) {
    Spacer(Modifier
        .size(40.dp)
        .background(this, CircleShape)
        .border(Dp.Hairline, contrastAgainst(current_colour), CircleShape)
        .clickable {
            onSelected()
        }
    )
}
