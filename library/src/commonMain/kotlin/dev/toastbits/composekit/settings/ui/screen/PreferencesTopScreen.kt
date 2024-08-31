package dev.toastbits.composekit.settings.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.Screen
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.composekit.settings.ui.component.PreferencesGroupPreview
import dev.toastbits.composekit.utils.composable.pauseableInfiniteRepeatableAnimation
import dev.toastbits.composekit.utils.composable.wave.LocalWaveLineAreaState
import dev.toastbits.composekit.utils.composable.wave.WaveLineAreaState
import kotlin.math.roundToInt

data class PreferencesTopScreen(
    private val groups: List<PreferencesGroup>,
    private val getTitle: @Composable () -> String
): Screen {
    private var waveMillis: Int = 0

    override val title: String
        @Composable get() = getTitle()

    @Composable
    override fun Content(navigator: Navigator, modifier: Modifier, contentPadding: PaddingValues) {
        val wavePeriod: Int = 2000
        val waveState: WaveLineAreaState =
            pauseableInfiniteRepeatableAnimation(
                start = 0f,
                end = 1f,
                period = wavePeriod,
                initialOffsetMillis = waveMillis
            )

        waveMillis = (waveState.value * wavePeriod).roundToInt()

        CompositionLocalProvider(LocalWaveLineAreaState provides waveState) {
            ScrollBarLazyColumn(
                modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = contentPadding
            ) {
                items(groups) { group ->
                    val isCurrent: Boolean =
                        navigator.getMostRecentOfOrNull { (it as? PreferencesGroupScreen)?.group == group } != null

                    PreferencesGroupPreview(
                        group,
                        highlight = isCurrent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val currentGroup: PreferencesGroup? = (navigator.currentScreen as? PreferencesGroupScreen)?.group
                                if (currentGroup == group) {
                                    return@clickable
                                }
                                else if (currentGroup != null) {
                                    navigator.replaceScreen(PreferencesGroupScreen(group))
                                }
                                else {
                                    navigator.pushScreen(PreferencesGroupScreen(group))
                                }
                            }
                    )
                }
            }
        }
    }
}
