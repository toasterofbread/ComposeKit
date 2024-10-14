package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle

@Composable
actual fun PlatformTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    text_style: TextStyle,
    background_colour: Color,
    shape: Shape,
    content_padding: PaddingValues
) {
    TextField(value, onValueChange, modifier, textStyle = text_style)
}
