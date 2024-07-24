package dev.toastbits.composekit.test

import android.content.Context
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.toastbits.composekit.platform.ApplicationContext
import dev.toastbits.composekit.platform.PlatformContext

@Composable
actual fun rememberTestPlatformContext(): PlatformContext {
    val context: Context = LocalContext.current
    val on_back_pressed_dispatcher_owner: OnBackPressedDispatcherOwner? = LocalOnBackPressedDispatcherOwner.current

    return remember(context) {
        val _application_context: ApplicationContext = mock {
            every { simulateBackPress() } calls {
                val dispatcher: OnBackPressedDispatcher =
                    on_back_pressed_dispatcher_owner?.onBackPressedDispatcher
                        ?: throw RuntimeException("on_back_pressed_dispatcher_owner is null")
                dispatcher.onBackPressed()
            }
        }

        mock {
            every { ctx } returns context
            every { application_context } returns _application_context
        }
    }
}
