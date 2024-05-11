package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.geometry.Offset
import dev.toastbits.composekit.platform.composable.platformClickableWithOffset

// https://gist.github.com/stevdza-san/ff9dbec0e072d8090e1e6d16e6b73c91
@Composable
fun LinkifyText(
    text: String,
    highlight_colour: Color,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val annotated_string: AnnotatedString =
        buildAnnotatedString {
            append(text)

            var head: Int = 0
            while (true) {
                val link_start: Int = text.indexOf("https://", head)
                if (link_start == -1) {
                    break
                }

                val link_length: Int = text.substring(link_start).indexOfFirst { it.isWhitespace() }.takeIf { it != -1 } ?: (text.length - link_start)
                val link_end: Int = link_start + link_length

                addStyle(
                    style = SpanStyle(color = highlight_colour),
                    start = link_start,
                    end = link_end
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = text.substring(link_start, link_end),
                    start = link_start,
                    end = link_end
                )

                head = link_end
            }
        }

    val uri_handler: UriHandler = LocalUriHandler.current
    var layout_result: TextLayoutResult? by remember { mutableStateOf(null) }

    ObservableSelectionContainer { selection: IntRange? ->
        Text(
            text = annotated_string,
            style = style,
            onTextLayout = { layout_result = it },
            modifier = modifier
                .platformClickableWithOffset(
                    onClick = { position ->
                        if (selection != null) {
                            return@platformClickableWithOffset
                        }

                        val offset: Int =
                            layout_result?.getOffsetForPosition(position)
                            ?: return@platformClickableWithOffset

                        val link: String =
                            annotated_string
                                .getStringAnnotations(
                                    start = offset,
                                    end = offset,
                                    tag = "URL"
                                )
                                .firstOrNull()
                                ?.item
                                ?: return@platformClickableWithOffset

                        uri_handler.openUri(link)
                        return@platformClickableWithOffset
                    }
                )
        )
    }
}
