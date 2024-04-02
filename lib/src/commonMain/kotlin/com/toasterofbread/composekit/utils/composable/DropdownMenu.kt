package com.toasterofbread.composekit.utils.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.toasterofbread.composekit.utils.common.thenIf

@Composable
fun LargeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    item_count: Int,
    selected: Int?,
    itemContent: @Composable (Int) -> Unit,
    modifier: Modifier = Modifier,
    container_colour: Color = MaterialTheme.colorScheme.surface,
    selected_border_colour: Color = MaterialTheme.colorScheme.outlineVariant,
    onSelected: (index: Int) -> Unit
) {
    require(selected == null || selected in 0 until item_count) {
        "selected=$selected, item_count=$item_count"
    }

    if (!expanded) {
        return
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier,
            shape = RoundedCornerShape(12.dp),
            color = container_colour
        ) {
            val list_state = rememberLazyListState()
            LaunchedEffect(Unit) {
                if (selected != null) {
                    list_state.scrollToItem(index = selected)
                }
            }

            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall) {
                LazyColumn(modifier = Modifier.fillMaxWidth(), state = list_state) {
                    items(item_count) { index ->
                        Box(
                            Modifier
                                .clickable { onSelected(index) }
                                .fillMaxWidth()
                                .padding(8.dp)
                                .thenIf(index == selected) {
                                    border(1.dp, selected_border_colour, RoundedCornerShape(16.dp))
                                }
                                .padding(8.dp)
                        ) {
                            itemContent(index)
                        }

                        if (index + 1 < item_count) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}
