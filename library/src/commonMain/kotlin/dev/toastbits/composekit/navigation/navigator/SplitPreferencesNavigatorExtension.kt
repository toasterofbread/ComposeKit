package dev.toastbits.composekit.navigation.navigator

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.composekit.settings.ui.screen.PreferencesGroupScreen
import dev.toastbits.composekit.settings.ui.screen.PreferencesTopScreen
import dev.toastbits.composekit.settings.ui.screen.SplitPreferencesScreen

object SplitPreferencesNavigatorExtension: NavigatorExtension {
    internal val GROUPS_MIN_WIDTH: Dp = 300.dp

    @Composable
    override fun rememberCurrentScreenOverride(navigator: Navigator, constraints: BoxWithConstraintsScope): Screen? {
        var waitingForSpace: Boolean by remember { mutableStateOf(false) }
        var previousScreen: SplitPreferencesScreen? by remember { mutableStateOf(null) }

        val current: PreferencesGroupScreen? = (navigator.currentScreen as? PreferencesGroupScreen)
        val last: PreferencesTopScreen? = navigator.getMostRecentOfOrNull { it is PreferencesTopScreen } as PreferencesTopScreen?

        if (current == null || last == null) {
            waitingForSpace = false
            previousScreen = null
            return null
        }

        if (!constraints.isAreaLargeEnough()) {
            waitingForSpace = true
            previousScreen = null
            return null
        }

        val screen: SplitPreferencesScreen =
            remember(waitingForSpace, last, current) {
                previousScreen?.animateExit = false
                SplitPreferencesScreen(
                    last,
                    current,
                    animateEntrance = previousScreen == null && !waitingForSpace
                )
            }

        previousScreen = screen
        return screen
    }

    override fun shouldSkipScreenTransitionAnimation(from: Screen, to: Screen): Boolean =
        from is SplitPreferencesScreen && to is SplitPreferencesScreen

    private fun BoxWithConstraintsScope.isAreaLargeEnough(): Boolean {
        val availableGroupsWidth: Dp = maxWidth * (1f - SplitPreferencesScreen.GROUP_FILL_FRACTION)
        return availableGroupsWidth >= GROUPS_MIN_WIDTH
    }
}
