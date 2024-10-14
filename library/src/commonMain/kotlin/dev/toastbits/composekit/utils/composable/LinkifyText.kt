package dev.toastbits.composekit.utils.composable

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import dev.toastbits.composekit.platform.LocalContext
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.composable.platformClickableWithOffset

// https://gist.github.com/stevdza-san/ff9dbec0e072d8090e1e6d16e6b73c91
@Composable
fun LinkifyText(
    text: String,
    highlight_colour: Color,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val context: PlatformContext = LocalContext.current
    var layout_result: TextLayoutResult? by remember { mutableStateOf(null) }
    
    val annotated_string: AnnotatedString =
        buildAnnotatedString {
            append(text)

            if (!context.canOpenUrl()) {
                return@buildAnnotatedString
            }

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

    ObservableSelectionContainer { selection: IntRange? ->
        Text(
            text = annotated_string,
            style = style,
            onTextLayout = { layout_result = it },
            modifier = modifier
                .platformClickableWithOffset(
                    onClick = { position ->
                        if (!context.canOpenUrl() || selection != null) {
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

                        context.openUrl(link)
                    }
                )
        )
    }
}
