@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package dev.toastbits.composekit.utils.composable

import androidx.compose.foundation.text.*
import androidx.compose.foundation.text.selection.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.*
import androidx.compose.ui.util.fastForEach

@Composable
fun ObservableSelectionContainer(
    modifier: Modifier = Modifier,
    onSelectionChange: (IntRange?) -> Unit = {},
    content: @Composable (IntRange?) -> Unit
) {
    var selection: Selection? by remember { mutableStateOf(null) }
    SelectionContainer(
        modifier = modifier,
        selection = selection,
        onSelectionChange = {
            selection = it
            onSelectionChange(selection?.getRange())
        },
        children = {
            content(selection?.getRange())
        }
    )
}

internal fun Selection.getRange(): IntRange? {
    val start_offset: Int = minOf(start.offset, end.offset)
    val end_offset: Int = maxOf(start.offset, end.offset)

    if (start_offset == end_offset) {
        return null
    }

    return start_offset .. end_offset
}

@Composable
internal fun SelectionContainer(
    /** A [Modifier] for SelectionContainer. */
    modifier: Modifier = Modifier,
    /** Current Selection status.*/
    selection: Selection?,
    /** A function containing customized behaviour when selection changes. */
    onSelectionChange: (Selection?) -> Unit,
    children: @Composable () -> Unit
) {
    val registrarImpl = rememberSaveable(saver = SelectionRegistrarImpl.Saver) {
        SelectionRegistrarImpl()
    }

    val manager = remember { SelectionManager(registrarImpl) }

    manager.hapticFeedBack = LocalHapticFeedback.current
    manager.clipboardManager = LocalClipboardManager.current
    manager.textToolbar = LocalTextToolbar.current
    manager.onSelectionChange = onSelectionChange
    manager.selection = selection

    ContextMenuArea(manager) {
        CompositionLocalProvider(LocalSelectionRegistrar provides registrarImpl) {
            // Get the layout coordinates of the selection container. This is for hit test of
            // cross-composable selection.
            SimpleLayout(modifier = modifier.then(manager.modifier)) {
                children()
                if (manager.isInTouchMode &&
                    manager.hasFocus &&
                    !manager.isTriviallyCollapsedSelection()
                ) {
                    manager.selection?.let {
                        listOf(true, false).fastForEach { isStartHandle ->
                            val observer = remember(isStartHandle) {
                                manager.handleDragObserver(isStartHandle)
                            }

                            val positionProvider: () -> Offset = remember(isStartHandle) {
                                if (isStartHandle) {
                                    { manager.startHandlePosition ?: Offset.Unspecified }
                                } else {
                                    { manager.endHandlePosition ?: Offset.Unspecified }
                                }
                            }

                            val direction = if (isStartHandle) {
                                it.start.direction
                            } else {
                                it.end.direction
                            }

                            SelectionHandle(
                                offsetProvider = positionProvider,
                                isStartHandle = isStartHandle,
                                direction = direction,
                                handlesCrossed = it.handlesCrossed,
                                modifier = Modifier.pointerInput(observer) {
                                    detectDownAndDragGesturesWithObserver(observer)
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(manager) {
        onDispose {
            manager.onRelease()
            manager.hasFocus = false
        }
    }
}
