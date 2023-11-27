package com.toasterofbread.composekit.utils.composable

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.toasterofbread.composekit.utils.common.associateNotNull
import java.util.regex.Pattern

private typealias UrlInfo = Triple<String, Int, Int>

// https://stackoverflow.com/a/66235329
@Composable
fun LinkifyText(
    text: String,
    highlight_colour: Color,
    modifier: Modifier = Modifier,
    colour: Color = LocalContentColor.current,
    style: TextStyle = LocalTextStyle.current
) {
	val uri_handler: UriHandler = LocalUriHandler.current
    val density: Density = LocalDensity.current

	val urls: List<UrlInfo> = remember(text) { text.extractURLs() }
    val annotated_string: AnnotatedString = remember(urls) {
        buildAnnotatedString {
            var prev = 0
            for ((i, url) in urls.withIndex()) {
                append(text.substring(prev, url.second))
                prev = url.third + 1
                appendInlineContent(i.toString(), url.first)
            }
            if (prev < text.length) {
                append(text.substring(prev))
            }
        }
    }

    val link_sizes: MutableMap<UrlInfo, IntSize> = remember(urls) { mutableStateMapOf() }
    for (url in urls) {
        MeasureUnconstrainedView({ Text(url.first, style = style) }) {
            link_sizes[url] = it
        }
    }

    Text(
        text = annotated_string,
        color = colour,
        style = style,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        inlineContent = urls.withIndex().associateNotNull {
            val (i, url) = it
            val link_size: IntSize = link_sizes[url] ?: return@associateNotNull null
            Pair(
                i.toString(),
                InlineTextContent(
                    with (density) {
                        Placeholder(link_size.width.toSp(), link_size.height.toSp(), placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter)
                    }
                ) {
                    Text(
                        url.first,
                        Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    uri_handler.openUri(url.first)
                                }
                            },
                        color = highlight_colour,
                        textDecoration = TextDecoration.Underline
                    )
                }
            )
        }
    )
}

private val URL_PATTERN: Pattern = Pattern.compile(
    "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
    Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
)

private fun String.extractURLs(): List<UrlInfo> {
    val matcher = URL_PATTERN.matcher(this)
    var start: Int
    var end: Int
    val links = arrayListOf<UrlInfo>()

    while (matcher.find()) {
        start = matcher.start(1)
        end = matcher.end()

        var url = substring(start, end)
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }

        links.add(UrlInfo(url, start, end))
    }
    return links
}
