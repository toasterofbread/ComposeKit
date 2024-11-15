package dev.toastbits.composekit.settings.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.navigation.compositionlocal.LocalNavigator
import dev.toastbits.composekit.navigation.navigator.CurrentScreen
import dev.toastbits.composekit.navigation.navigator.BaseNavigator
import dev.toastbits.composekit.navigation.navigator.Navigator
import dev.toastbits.composekit.navigation.navigator.replaceScreenUpTo
import dev.toastbits.composekit.navigation.screen.ResponsiveTwoPaneScreen
import dev.toastbits.composekit.navigation.screen.Screen
import dev.toastbits.composekit.platform.composable.ScrollBarLazyColumn
import dev.toastbits.composekit.platform.preferences.PreferencesGroup
import dev.toastbits.composekit.settings.ui.component.PreferencesGroupPreview
import dev.toastbits.composekit.utils.composable.pane.model.ResizablePaneContainerParamsProvider
import dev.toastbits.composekit.utils.composable.pauseableInfiniteRepeatableAnimation
import dev.toastbits.composekit.utils.composable.wave.LocalWaveLineAreaState
import dev.toastbits.composekit.utils.composable.wave.WaveLineAreaState
import kotlin.math.roundToInt

data class PreferencesTopScreen(
    private val groups: List<PreferencesGroup>,
    private val getTitle: @Composable () -> String,
    private val paneParams: ResizablePaneContainerParamsProvider = ResizablePaneContainerParamsProvider.default()
): ResponsiveTwoPaneScreen<Screen>(
    initialStartPaneRatio = 0.4f,
    paneParams = paneParams
) {
    private var waveMillis: Int = 0
    private var firstLaunch: Boolean = true

    override val title: String
        @Composable get() = getTitle()

    private val internalNavigator: BaseNavigator =
        object : BaseNavigator(Screen.EMPTY) {
            override fun getNavigateBackwardCount(): Int =
                if (isDisplayingBothPanes) (super.getNavigateBackwardCount() - 1).coerceAtLeast(0)
                else super.getNavigateBackwardCount()
        }

    private val currentScreen: Screen?
        get() = internalNavigator.currentScreen.takeUnless { it == Screen.EMPTY }

    override var alwaysShowEndPane: Boolean = true

    @Composable
    override fun getCurrentData(): Screen? = currentScreen

    override fun shouldSkipFormFactorTransition(from: Boolean, to: Boolean): Boolean =
        currentScreen == null

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

        LaunchedEffect(Unit) {
            if (!firstLaunch) {
                alwaysShowEndPane = false
                return@LaunchedEffect
            }
            firstLaunch = false

            if (isDisplayingBothPanes && groups.isNotEmpty()) {
                openGroup(groups.first())
                alwaysShowEndPane = true
            }
            else {
                alwaysShowEndPane = false
            }
        }

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
                                    openGroup(group)
                                }
                            }
                    )
                }
            }
        }
    }

    private fun openGroup(group: PreferencesGroup) {
        internalNavigator.replaceScreenUpTo(PreferencesGroupScreen(group), PreferencesGroupScreen::class)
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
