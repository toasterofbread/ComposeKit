package dev.toastbits.composekit.utils.composable.pane.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.platform.preferences.impl.ComposeKitSettings
import kotlinx.serialization.Serializable

@Serializable
sealed interface InitialPaneRatioSource {
    fun getInitialPaneRatio(settings: ComposeKitSettings?, availableSpace: Dp): Float
    fun update(settings: ComposeKitSettings, ratio: Float, availableSpace: Dp)

    @Serializable
    data class Ratio(val ratio: Float): InitialPaneRatioSource {
        override fun getInitialPaneRatio(
            settings: ComposeKitSettings?,
            availableSpace: Dp
        ): Float = ratio

        override fun update(
            settings: ComposeKitSettings,
            ratio: Float,
            availableSpace: Dp
        ) {}
    }

    @Serializable
    data class SizeDp(val sizeDp: Float): InitialPaneRatioSource {
        override fun getInitialPaneRatio(
            settings: ComposeKitSettings?,
            availableSpace: Dp
        ): Float =
            sizeDp.dp / availableSpace

        override fun update(
            settings: ComposeKitSettings,
            ratio: Float,
            availableSpace: Dp
        ) {}
    }

    @Serializable
    data class Remembered(
        val key: String,
        val default: Ratio
    ): InitialPaneRatioSource {
        override fun getInitialPaneRatio(settings: ComposeKitSettings?, availableSpace: Dp): Float =
            (
                settings?.Interface?.REMEMBERED_INITIAL_PANE_RATIOS?.get()?.get(key) ?: default
            ).getInitialPaneRatio(settings, availableSpace)

        override fun update(settings: ComposeKitSettings, ratio: Float, availableSpace: Dp) {
            if (!settings.Interface.REMEMBER_INITIAL_PANE_RATIOS.get()) {
                return
            }

            val ratios: MutableMap<String, InitialPaneRatioSource> =
                settings.Interface.REMEMBERED_INITIAL_PANE_RATIOS.get().toMutableMap()

            ratios[key] =
                when (settings.Interface.INITIAL_PANE_RATIO_REMEMBER_MODE.get()) {
                    RememberMode.RATIO -> Ratio(ratio)
                    RememberMode.SIZE_DP -> SizeDp((availableSpace * ratio).value)
                }

            settings.Interface.REMEMBERED_INITIAL_PANE_RATIOS.set(ratios)
        }

        enum class RememberMode {
            RATIO,
            SIZE_DP;

            companion object {
                val DEFAULT: RememberMode = RATIO
            }
        }
    }
}
