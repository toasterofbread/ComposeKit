package dev.toastbits.composekit.navigation.navigator

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import dev.toastbits.composekit.navigation.Screen

interface NavigatorExtension {
    @Composable
    fun rememberCurrentScreenOverride(navigator: Navigator, constraints: BoxWithConstraintsScope): Screen?

    fun shouldSkipScreenTransitionAnimation(from: Screen, to: Screen): Boolean = false
}
