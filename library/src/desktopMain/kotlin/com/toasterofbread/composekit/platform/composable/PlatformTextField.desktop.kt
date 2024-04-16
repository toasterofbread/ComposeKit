package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
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
    BasicTextField(
        value,
        onValueChange,
        modifier.background(background_colour, shape).padding(content_padding),
        textStyle = text_style.copy(
            color = text_style.color.takeOrElse { LocalContentColor.current }
        )
    )
}
