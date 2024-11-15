package dev.toastbits.composekit.settings.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.compositionlocal.LocalNavigator
import dev.toastbits.composekit.navigation.navigator.CurrentScreen
import dev.toastbits.composekit.navigation.navigator.ExtendableNavigator
import dev.toastbits.composekit.navigation.screen.Screen
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.navigation.navigator.replaceScreenUpTo
import dev.toastbits.composekit.navigation.screen.ResponsiveTwoPaneScreen
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.composekit.settings.ui.component.PreferencesGroupPreview
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParams
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParamsData
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParamsProvider
import dev.toastbits.composekit.utils.composable.pauseableInfiniteRepeatableAnimation
import dev.toastbits.composekit.utils.composable.wave.LocalWaveLineAreaState
import dev.toastbits.composekit.utils.composable.wave.WaveLineAreaState
import kotlin.math.roundToInt

data class PreferencesTopScreen(
    private val groups: List<PreferencesGroup>,
    private val getTitle: @Composable () -> String,
    private val paneParams: ResizablePaneContainerParamsProvider = ResizablePaneContainerParamsData()
): ResponsiveTwoPaneScreen<Screen>(
    initialStartPaneRatio = 0.4f,
    paneParams = paneParams
) {
    private var waveMillis: Int = 0

    override val title: String
        @Composable get() = getTitle()

    private val internalNavigator: ExtendableNavigator =
        ExtendableNavigator(Screen.EMPTY)

    @Composable
    override fun getCurrentData(): Screen? =
        internalNavigator.currentScreen.takeUnless { it == Screen.EMPTY }

    @Composable
    override fun PrimaryPane(data: Screen?, contentPadding: PaddingValues, modifier: Modifier) {
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
                val activeGroup: PreferencesGroup? =
                    (internalNavigator.getMostRecentOfOrNull { it is PreferencesGroupScreen } as PreferencesGroupScreen?)?.group

                itemsIndexed(groups) { index, group ->
                    PreferencesGroupPreview(
                        group,
                        highlight = group == activeGroup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val currentGroup: PreferencesGroup? = (internalNavigator.currentScreen as? PreferencesGroupScreen)?.group
                                if (currentGroup == group) {
                                    return@clickable
                                }
                                else {
                                    internalNavigator.replaceScreenUpTo(PreferencesGroupScreen(group), PreferencesGroupScreen::class)
                                }
                            }
                    )
                }
            }
        }
    }

    @Composable
    override fun SecondaryPane(data: Screen?, contentPadding: PaddingValues, modifier: Modifier) {
        val navigator: Navigator = LocalNavigator.current

        DisposableEffect(Unit) {
            navigator.addChild(internalNavigator)
            onDispose {
                navigator.removeChild(internalNavigator)
            }
        }

        internalNavigator.CurrentScreen(modifier, contentPadding)
    }
}
