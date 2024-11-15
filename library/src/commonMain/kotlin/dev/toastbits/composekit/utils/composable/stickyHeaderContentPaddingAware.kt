// https://gitlab.com/-/snippets/3604982

package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

private data class StickyType(val contentType: Any?)

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.stickyHeaderContentPaddingAware(
    listState: LazyListState,
    key: Any,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit
) {
    stickyHeader(
        key = key,
        contentType = StickyType(contentType),
        content = {
            Layout(content = { content() }) { measurables, constraints ->
                val placeable = measurables.first().measure(constraints)
                val width = constraints.constrainWidth(placeable.width)
                val height = constraints.constrainHeight(placeable.height)
                layout(width, height) {
                    val posY = coordinates?.positionInParent()?.y?.toInt() ?: 0
                    val paddingTop = listState.layoutInfo.beforeContentPadding
                    var top = (paddingTop - posY).coerceIn(0, paddingTop)
                    if (top > 0) {
                        val second = listState.layoutInfo.visibleItemsInfo
                            .filter { it.contentType is StickyType }
                            .getOrNull(1)
                        if (second != null && second.key != key) {
                            val secondOffset = second.offset
                            if (secondOffset <= height) {
                                top -= (height - secondOffset)
                            }
                        }
                    }
                    placeable.place(0, top)
                }
            }
        }
    )
}
