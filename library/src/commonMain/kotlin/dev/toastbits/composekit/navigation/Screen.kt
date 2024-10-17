package dev.toastbits.composekit.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.toastbits.composekit.navigation.navigator.Navigator

interface Screen {
    val title: String?
        @Composable get() = null

    @Composable
    fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues)

    fun onClosed() {}

    companion object {
        val EMPTY: Screen =
            object : Screen {
                @Composable
                override fun Content(
                    navigator: Navigator,
                    modifier: Modifier,
                    contentPadding: PaddingValues
                ) {}
            }
    }
}
