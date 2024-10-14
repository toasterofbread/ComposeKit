package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

@Composable
fun WidthShrinkText(
    string: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    inline_content: Map<String, InlineTextContent> = mapOf(),
    alignment: TextAlign? = null,
    max_lines: Int = 1
) {
    val density: Density = LocalDensity.current
    val delta: Float = 0.075f

    var current_style: TextStyle by remember(style) { mutableStateOf(style) }
    var draw_content: Boolean by remember(style) { mutableStateOf(false) }

    var box_size: DpSize by remember { mutableStateOf(DpSize.Zero) }

    Box(
        modifier
            .onSizeChanged {
                box_size = with (density) {
                    DpSize(it.width.toDp(), it.height.toDp())
                }
            }
            .width(IntrinsicSize.Min)
    ) {
        Box(Modifier.requiredSize(0.dp)) {
            val large_style: TextStyle = current_style.shiftSize(delta, style.fontSize)

            Text(
                string,
                Modifier.requiredSize(box_size).drawWithContent {},
                color = Color.Green,
                maxLines = max_lines,
                style = large_style,
                inlineContent = inline_content,
                textAlign = alignment,
                overflow = TextOverflow.Clip,
                onTextLayout = { layout_result ->
                    if (!layout_result.didOverflowWidth && !layout_result.didOverflowHeight) {
                        current_style = large_style
                    }
                }
            )
        }

        Text(
            string,
            Modifier
                .drawWithContent { if (draw_content) drawContent() }
                .fillMaxWidth(),
            maxLines = max_lines,
            style = current_style,
            inlineContent = inline_content,
            textAlign = alignment,
            overflow = TextOverflow.Clip,
            onTextLayout = { layout_result ->
                if (layout_result.didOverflowWidth || layout_result.didOverflowHeight) {
                    current_style = current_style.shiftSize(-delta, style.fontSize)
                }
                else {
                    draw_content = true
                }
            }
        )
    }
}

@Composable
fun WidthShrinkText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    alignment: TextAlign? = null,
    max_lines: Int = 1
) {
    WidthShrinkText(
        AnnotatedString(text),
        modifier,
        style,
        alignment = alignment,
        max_lines = max_lines
    )
}

@Composable
fun WidthShrinkText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    colour: Color = LocalContentColor.current,
    alignment: TextAlign? = null
) {
    WidthShrinkText(
        text,
        modifier,
        LocalTextStyle.current.copy(fontSize = fontSize, fontWeight = fontWeight, color = colour),
        alignment
    )
}

private fun TextStyle.shiftSize(by: Float, max: TextUnit): TextStyle =
    copy(
        fontSize = (fontSize * (1.0 + by)).let { new ->
            if (new > max) max
            else if (new < 1.sp) 1.sp
            else new
        }
    )
