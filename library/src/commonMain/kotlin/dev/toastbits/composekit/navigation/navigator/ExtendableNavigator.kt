package dev.toastbits.composekit.navigation.navigator

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import dev.toastbits.composekit.navigation.screen.Screen
import dev.toastbits.composekit.navigation.compositionlocal.LocalNavigator
import dev.toastbits.composekit.navigation.content.NavigatorContent
import dev.toastbits.composekit.platform.composable.BackHandler
import dev.toastbits.composekit.utils.composable.crossfade.SkippableCrossfade

open class ExtendableNavigator(
    initialScreen: Screen,
    private val extensions: List<NavigatorExtension> = emptyList()
): Navigator {
    private val stack: MutableList<Screen> = mutableStateListOf(initialScreen)

    private var _currentScreenIndex: Int by mutableStateOf(0)
    private var currentScreenIndex: Int
        get() = _currentScreenIndex
        set(value) {
            val newIndex: Int = value.coerceIn(0 until stack.size)
            val oldIndex: Int = _currentScreenIndex

            _currentScreenIndex = newIndex

            if (stack[oldIndex] != stack[newIndex]) {
                stack[oldIndex].onClosed()
            }
        }

    private val childNavigators: MutableList<Navigator> = mutableListOf()
    private val currentChildNavigator: Navigator? get() = childNavigators.lastOrNull()

    override val currentScreen: Screen
        get() = stack[currentScreenIndex]

    override fun pushScreen(screen: Screen, skipIfSameClass: Boolean) {
        if (skipIfSameClass && screen::class == currentScreen::class) {
            return
        }

        stack.removeAllButFirst(currentScreenIndex + 1)
        stack.add(screen)
        currentScreenIndex++
    }

    override fun replaceScreen(screen: Screen) {
        stack.removeAllButFirst(currentScreenIndex)
        stack.add(screen)
    }

    override fun replaceScreenUpTo(screen: Screen, isLastScreenToReplace: (Screen) -> Boolean) {
        for (i in currentScreenIndex downTo 0) {
            if (isLastScreenToReplace(stack[i])) {
                stack.add(screen)
                currentScreenIndex = i
                stack.removeAllButFirst(i, 1)
                return
            }
        }

        pushScreen(screen)
    }

    override fun canNavigateForward(): Boolean =
        currentScreenIndex + 1 < stack.size || currentChildNavigator?.canNavigateForward() == true

    override fun canNavigateBackward(): Boolean =
        currentScreenIndex > 0 || currentChildNavigator?.canNavigateBackward() == true

    override fun navigateForward(by: Int) {
        require(by >= 0)

        if (currentChildNavigator?.canNavigateForward() == true) {
            currentChildNavigator?.navigateForward(by)
        }
        else {
            currentScreenIndex += by
        }
    }

    override fun navigateBackward(by: Int) {
        require(by >= 0)

        if (currentChildNavigator?.canNavigateBackward() == true) {
            currentChildNavigator?.navigateBackward(by)
        }
        else {
            currentScreenIndex -= by
        }
    }

    override fun peekRelative(offset: Int): Screen? =
        stack.getOrNull(currentScreenIndex + offset)

    override fun getMostRecentOfOrNull(predicate: (Screen) -> Boolean): Screen? {
        for (i in currentScreenIndex downTo 0) {
            if (predicate(stack[i])) {
                return stack[i]
            }
        }
        return null
    }

    override fun addChild(navigator: Navigator) {
        require(navigator != this) { "Cannot add navigator as child of itself" }
        childNavigators.add(navigator)
    }

    override fun removeChild(navigator: Navigator) {
        require(navigator != this) { "Cannot add navigator as child of itself" }
        childNavigators.remove(navigator)
    }

    @Composable
    override fun CurrentScreen(
        modifier: Modifier,
        contentPadding: PaddingValues,
        render: @Composable (Modifier, PaddingValues, @Composable (Modifier, PaddingValues) -> Unit) -> Unit
    ) {
        NavigatorContent(this, modifier) {
            CompositionLocalProvider(LocalNavigator provides this) {
                render(it, contentPadding) { innerModifier, innerContentPadding ->
                    BoxWithConstraints {
                        val overrideCurrentScreen: Screen? = extensions.firstNotNullOfOrNull {
                            it.rememberCurrentScreenOverride(this@ExtendableNavigator, this@BoxWithConstraints)
                        }

                        SkippableCrossfade(
                            overrideCurrentScreen ?: currentScreen,
                            shouldSkipTransition = { a, b ->
                                extensions.any { it.shouldSkipScreenTransitionAnimation(a, b) }
                            }
                        ) { screen ->
                            screen.Content(this@ExtendableNavigator, innerModifier, innerContentPadding)
                        }
                    }
                }
            }
        }

        BackHandler(canNavigateBackward()) {
            navigateBackward()
        }
    }

    override fun handleKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyUp) {
            return false
        }

        when (keyEvent.key) {
            Key.Escape -> navigateBackward()
            else -> return false
        }
        return true
    }
}

private fun MutableList<*>.removeAllButFirst(n: Int, keepLast: Int = 0) {
    for (i in 0 until size - n - keepLast) {
        removeAt(size - 1 - keepLast)
    }
}
