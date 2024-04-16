package dev.toastbits.composekit.platform.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle

@Composable
expect fun PlatformTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    text_style: TextStyle = LocalTextStyle.current,
    background_colour: Color = Color.Unspecified,
    shape: Shape = RectangleShape,
    content_padding: PaddingValues = PaddingValues()
)
