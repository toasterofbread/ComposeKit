package dev.toastbits.composekit.navigation.compositionlocal

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import dev.toastbits.composekit.navigation.navigator.Navigator

val LocalNavigator: ProvidableCompositionLocal<Navigator> =
    staticCompositionLocalOf { throw IllegalStateException() }
