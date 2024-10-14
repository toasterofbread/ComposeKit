package dev.toastbits.composekit.navigation.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import dev.toastbits.composekit.navigation.navigator.Navigator

@Composable
internal actual fun NavigatorContent(
    navigator: Navigator,
    modifier: Modifier,
    content: @Composable (Modifier) -> Unit
) {
    content(
        modifier.onPointerEvent(PointerEventType.Press) { event ->
            val index: Int = event.button?.index ?: return@onPointerEvent
            navigator.onButtonPress(index)
        }
    )
}

private fun Navigator.onButtonPress(button: Int) {
    when (button) {
        PointerButton.Forward.index, 6 -> navigateForward()
        PointerButton.Back.index, 5 -> navigateBackward()
    }
}
