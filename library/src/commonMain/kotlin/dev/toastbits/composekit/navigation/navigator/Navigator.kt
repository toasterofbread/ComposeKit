package dev.toastbits.composekit.navigation.navigator

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.composekit.platform.composable.BackHandler
import kotlin.reflect.KClass

interface Navigator {
    @Composable
    fun CurrentScreen(modifier: Modifier, contentPadding: PaddingValues, render: @Composable (Modifier, PaddingValues, @Composable (Modifier, PaddingValues) -> Unit) -> Unit)

    val currentScreen: Screen

    fun pushScreen(screen: Screen, skipIfSameClass: Boolean = false)
    fun replaceScreen(screen: Screen)

    fun canNavigateForward(): Boolean
    fun canNavigateBackward(): Boolean

    fun navigateForward(by: Int = 1)
    fun navigateBackward(by: Int = 1)

    fun peekRelative(offset: Int): Screen?
    fun getMostRecentOfOrNull(predicate: (Screen) -> Boolean): Screen?

    fun handleKeyEvent(keyEvent: KeyEvent): Boolean
}

@Composable
fun Navigator.CurrentScreen(modifier: Modifier, contentPadding: PaddingValues) {
    CurrentScreen(modifier, contentPadding) { innerModifier, innerContentPadding, content ->
        content(innerModifier, innerContentPadding)
    }

    BackHandler(canNavigateBackward()) {
        navigateBackward()
    }
}
