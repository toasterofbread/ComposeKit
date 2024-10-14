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
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.composekit.navigation.compositionlocal.LocalNavigator
import dev.toastbits.composekit.navigation.content.NavigatorContent

class ExtendableNavigator(
    initialScreen: Screen,
    private val extensions: List<NavigatorExtension> = listOf(SplitPreferencesNavigatorExtension)
): Navigator {
    private val stack: MutableList<Screen> = mutableStateListOf(initialScreen)
    private var _currentScreenIndex: Int by mutableStateOf(0)
    private var currentScreenIndex: Int
        get() = _currentScreenIndex
        set(value) {
            _currentScreenIndex = value.coerceIn(0 until stack.size)
        }

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

    override fun canNavigateForward(): Boolean = currentScreenIndex + 1 < stack.size

    override fun canNavigateBackward(): Boolean =  currentScreenIndex > 0

    override fun navigateForward(by: Int) {
        require(by >= 0)
        currentScreenIndex += by
    }

    override fun navigateBackward(by: Int) {
        require(by >= 0)
        currentScreenIndex -= by
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

    @Composable
    fun <T> SkippableCrossfade(
        state: T,
        shouldSkipTransition: (T, T) -> Boolean,
        modifier: Modifier = Modifier,
        content: @Composable (T) -> Unit
    ) {
        var crossfadeState: T by remember { mutableStateOf(state) }
        var currentState: T by remember { mutableStateOf(state) }
        var useCurrentState: Boolean by remember { mutableStateOf(false) }

        LaunchedEffect(state) {
            if (shouldSkipTransition(currentState, state)) {
                currentState = state
                useCurrentState = true
            }
            else {
                currentState = state
                crossfadeState = state
                useCurrentState = false
            }
        }

        Crossfade(crossfadeState, modifier) { s ->
            content(if (useCurrentState) currentState else s)
        }
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

private fun MutableList<*>.removeAllButFirst(n: Int) {
    for (i in 0 until size - n) {
        removeLast()
    }
}
