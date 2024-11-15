package dev.toastbits.composekit.navigation.navigator

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.navigation.screen.Screen
import kotlin.reflect.KClass

interface Navigator {
    @Composable
    fun CurrentScreen(modifier: Modifier, contentPadding: PaddingValues, render: @Composable (Modifier, PaddingValues, @Composable (Modifier, PaddingValues) -> Unit) -> Unit)

    val currentScreen: Screen

    fun pushScreen(screen: Screen, skipIfSameClass: Boolean = false)
    fun replaceScreen(screen: Screen)
    fun replaceScreenUpTo(screen: Screen, isLastScreenToReplace: (Screen) -> Boolean)

    fun getNavigateForwardCount(): Int
    fun getNavigateBackwardCount(): Int

    fun navigateForward(by: Int = 1)
    fun navigateBackward(by: Int = 1)

    fun peekRelative(offset: Int): Screen?
    fun getMostRecentOfOrNull(predicate: (Screen) -> Boolean): Screen?

    fun addChild(navigator: Navigator)
    fun removeChild(navigator: Navigator)
}

@Composable
fun Navigator.CurrentScreen(modifier: Modifier, contentPadding: PaddingValues) {
    CurrentScreen(modifier, contentPadding) { innerModifier, innerContentPadding, content ->
        content(innerModifier, innerContentPadding)
    }
}

fun Navigator.replaceScreenUpTo(screen: Screen, lastScreenToReplace: KClass<out Screen>) {
    replaceScreenUpTo(screen) { lastScreenToReplace.isInstance(it) }
}
