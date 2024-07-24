package dev.toastbits.composekit.test.platform.composable

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.runComposeUiTest
import dev.toastbits.composekit.platform.PlatformContext
import dev.toastbits.composekit.platform.composable.BackHandler
import dev.toastbits.composekit.platform.composable.onWindowBackPressed
import dev.toastbits.composekit.test.rememberTestPlatformContext
import kotlin.test.Test
import kotlin.test.assertFalse

// Doesn't seem to work on Android
class BackHandlerTest {
    @Test
    fun testBackHandler() = runComposeUiTest {
        var back_pressed: Boolean = false

        setContent {
            val context: PlatformContext = rememberTestPlatformContext()

            BackHandler {
                assertFalse(back_pressed)
                back_pressed = true
            }

            LaunchedEffect(Unit) {
                onWindowBackPressed(context)
            }
        }

        waitUntil(timeoutMillis = 500) { back_pressed }
    }
}
