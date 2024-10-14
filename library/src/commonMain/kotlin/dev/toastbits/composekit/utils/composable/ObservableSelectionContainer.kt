//@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.text.selection.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ObservableSelectionContainer(
    modifier: Modifier = Modifier,
    onSelectionChange: (IntRange?) -> Unit = {},
    content: @Composable (IntRange?) -> Unit
) {
//    var selection: Selection? by remember { mutableStateOf(null) }
//    val children: @Composable () -> Unit = {
//        content(selection?.getRange())
//    }
//
//    SelectionContainer(
//        modifier = modifier,
//        selection = selection,
//        onSelectionChange = {
//            selection = it
//            onSelectionChange(selection?.getRange())
//        },
//        children = children
//    )
}

//internal fun Selection.getRange(): IntRange? {
//    val start_offset: Int = minOf(start.offset, end.offset)
//    val end_offset: Int = maxOf(start.offset, end.offset)
//
//    if (start_offset == end_offset) {
//        return null
//    }
//
//    return start_offset .. end_offset
//}
