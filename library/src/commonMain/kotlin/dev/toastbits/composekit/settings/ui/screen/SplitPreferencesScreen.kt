package dev.toastbits.composekit.settings.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.screen.Screen
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.utils.common.copy

data class SplitPreferencesScreen(
    private val topScreen: PreferencesTopScreen,
    private val groupScreen: PreferencesGroupScreen,
    private val animateEntrance: Boolean = true,
    var animateExit: Boolean = true
): Screen {
    companion object {
        internal const val GROUP_FILL_FRACTION: Float = 0.6f
    }

    @Composable
    override fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues) {
        var showGroup: Boolean by remember { mutableStateOf(!animateEntrance) }

        val isCurrent: Boolean = navigator.currentScreen == groupScreen
        LaunchedEffect(isCurrent) {
            showGroup = isCurrent || !animateExit
        }

        Row(modifier) {
            topScreen.Content(
                navigator,
                Modifier.fillMaxWidth(1f).weight(1f),
                contentPadding.copy(end = 0.dp)
            )

            AnimatedVisibility(
                showGroup,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                groupScreen.Content(
                    navigator,
                    Modifier.fillMaxWidth(GROUP_FILL_FRACTION).fillMaxHeight(),
                    contentPadding.copy(start = 0.dp)
                )
            }
        }
    }
}