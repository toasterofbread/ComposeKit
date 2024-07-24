package dev.toastbits.composekit.test.settings.ui.theme

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.runComposeUiTest
import dev.toastbits.composekit.settings.ui.theme.ThemeManager
import dev.toastbits.composekit.settings.ui.theme.ThemeValuesData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeTest {
    @Test
    fun testThemeValues() = runComposeUiTest {
        val initial_colour: Color = Color.White
        val final_colour: Color = Color.Black

        // Not sure if this is needed?
        var finished: Boolean by mutableStateOf(false)

        setContent {
            val coroutine_scope: CoroutineScope = rememberCoroutineScope()
            val theme: ThemeManager = remember { ThemeManager(ThemeValuesData.singleColour(initial_colour), coroutine_scope) }

            LaunchedEffect(Unit) {
                assertEquals(theme.accent, initial_colour)
                theme.setTheme(ThemeValuesData.singleColour(final_colour))
                delay(1000)
                assertEquals(theme.accent, final_colour)
                finished = true
            }
        }

        waitUntil { finished }
    }
}
