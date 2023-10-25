package com.toasterofbread.toastercomposetools.settings.ui

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.toasterofbread.toastercomposetools.utils.common.compare
import com.toasterofbread.toastercomposetools.utils.common.contrastAgainst
import com.toasterofbread.toastercomposetools.utils.common.getContrasted
import com.catppuccin.Palette as Catppuccin

const val VIBRANT_ACCENT_CONTRAST: Float = 0.2f

abstract class Theme(
    system_theme_name: String
): ThemeData {
    val preview_active: Boolean get() = preview_theme_data != null
    override val name: String get() = getCurrentTheme().name

    override val background: Color get() = background_state.value
    override val on_background: Color get() = on_background_state.value
    override val accent: Color get() = accent_state.value
    val on_accent: Color get() = accent.getContrasted()
    val vibrant_accent: Color get() = makeVibrant(accent)

    val background_provider: () -> Color = { background_state.value }
    val on_background_provider: () -> Color = { on_background_state.value }
    val accent_provider: () -> Color = { accent_state.value }

    var current_theme_idx: Int by mutableStateOf(0)

    abstract fun saveThemes(themes: List<ThemeData>)
    abstract fun loadThemes(): List<ThemeData>

    abstract fun selectAccentColour(theme_data: ThemeData, thumbnail_colour: Color?): Color
    abstract fun getDarkColorScheme(): ColorScheme
    abstract fun getLightColorScheme(): ColorScheme

    fun makeVibrant(colour: Color, against: Color = background): Color {
        if (colour.compare(background) > 0.8f) {
            return colour.contrastAgainst(against, VIBRANT_ACCENT_CONTRAST)
        }
        return colour
    }

    fun getCurrentTheme(): ThemeData {
        preview_theme_data?.also {
            return it
        }

        if (current_theme_idx == 0) {
            return system_theme
        }
        else {
            return getLoadedThemes()[current_theme_idx - 1]
        }
    }

    private val default_themes = getDefaultThemes()
    private var loaded_themes: List<ThemeData>? by mutableStateOf(null)

    private var preview_theme_data: ThemeData? by mutableStateOf(null)
    private val system_theme = ColourSchemeThemeData(system_theme_name)

    private var thumbnail_colour: Color? by mutableStateOf(null)

    private val background_state: Animatable<Color, AnimationVector4D> by lazy { Animatable(getLoadedThemes().first().background) }
    private val on_background_state: Animatable<Color, AnimationVector4D> by lazy { Animatable(getLoadedThemes().first().on_background) }
    private val accent_state: Animatable<Color, AnimationVector4D> by lazy { Animatable(getLoadedThemes().first().accent) }

    private fun getLoadedThemes(): List<ThemeData> {
        if (loaded_themes == null) {
            loaded_themes = loadThemes().ifEmpty { default_themes }
        }
        return loaded_themes!!
    }

    override fun toStaticThemeData(name: String): StaticThemeData =
        StaticThemeData(
            name,
            background_state.targetValue,
            on_background_state.targetValue,
            accent_state.targetValue
        )

    fun getThemes(): List<ThemeData> {
        return listOf(system_theme) + getLoadedThemes()
    }
    fun getThemeCount(): Int = getLoadedThemes().size

    @Composable
    fun Update() {
        val dark_theme = isSystemInDarkTheme()
        system_theme.colour_scheme = remember(dark_theme) {
            if (dark_theme) getDarkColorScheme() else getLightColorScheme()
        }

        val data = getCurrentTheme()
        val background_colour = data.background
        val on_background_colour = data.on_background
        val accent_colour = selectAccentColour(data, thumbnail_colour)

        LaunchedEffect(background_colour) {
            background_state.animateTo(background_colour)
        }
        LaunchedEffect(on_background_colour) {
            on_background_state.animateTo(on_background_colour)
        }
        LaunchedEffect(accent_colour) {
            accent_state.animateTo(accent_colour)
        }
    }

    fun setPreviewThemeData(preview_data: ThemeData?) {
        preview_theme_data = preview_data
    }

    fun currentThumbnnailColourChanged(new_colour: Color?) {
        thumbnail_colour = new_colour
    }

    fun updateTheme(index: Int, theme: ThemeData) {
        loaded_themes = getLoadedThemes().toMutableList().also { it[index - 1] = theme }
        saveThemes(loaded_themes!!)
    }

    fun addTheme(theme: ThemeData, index: Int = getLoadedThemes().size + 1) {
        loaded_themes = getLoadedThemes().toMutableList().also { it.add(index - 1, theme) }
        saveThemes(loaded_themes!!)
    }

    fun removeTheme(index: Int) {
        if (getLoadedThemes().size == 1) {
            loaded_themes = default_themes
        }
        else {
            loaded_themes = getLoadedThemes().toMutableList().also { it.removeAt(index - 1) }
        }
        saveThemes(getLoadedThemes())
    }

    fun reloadThemes() {
        loaded_themes = loadThemes()
    }

    fun getDefaultThemes(): List<ThemeData> {
        val palette = Catppuccin.MOCHA

        return listOf(
            Pair(Color(palette.mauve.rgb), "mauve"),
            Pair(Color(palette.lavender.rgb), "lavender"),
            Pair(Color(palette.red.rgb), "red"),
            Pair(Color(palette.yellow.rgb), "yellow"),
            Pair(Color(palette.green.rgb), "green"),
            Pair(Color(palette.teal.rgb), "teal"),
            Pair(Color(palette.pink.rgb), "pink"),
            Pair(Color(palette.sapphire.rgb), "sapphire"),
            Pair(Color(palette.rosewater.rgb), "rosewater"),
            Pair(Color(palette.peach.rgb), "peach"),
            Pair(Color(palette.sky.rgb), "sky"),
            Pair(Color(palette.maroon.rgb), "maroon"),
            Pair(Color(palette.blue.rgb), "blue"),
            Pair(Color(palette.flamingo.rgb), "flamingo")
        ).map { accent ->
            StaticThemeData(
                "Catppuccin ${palette.name.replaceFirstChar { it.uppercaseChar() }} (${accent.second})",
                Color(palette.crust.rgb),
                Color(palette.text.rgb),
                accent.first
            )
        }
    }
}

interface ThemeData {
    val name: String
    val background: Color
    val on_background: Color
    val accent: Color

    fun isEditable(): Boolean = false
    fun toStaticThemeData(name: String): StaticThemeData
}

data class StaticThemeData(
    override val name: String,
    override val background: Color,
    override val on_background: Color,
    override val accent: Color
): ThemeData {
    fun serialise(): String {
        return "${background.toArgb()},${on_background.toArgb()},${accent.toArgb()},$name"
    }

    override fun isEditable(): Boolean = true
    override fun toStaticThemeData(name: String): StaticThemeData = copy(name = name)

    companion object {
        fun deserialise(data: String): StaticThemeData {
            val split = data.split(',', limit = 4)
            return StaticThemeData(
                split[3],
                Color(split[0].toInt()),
                Color(split[1].toInt()),
                Color(split[2].toInt())
            )
        }
    }
}

class ColourSchemeThemeData(
    override val name: String
): ThemeData {
    var colour_scheme: ColorScheme? by mutableStateOf(null)

    override val background: Color
        get() = colour_scheme!!.background
    override val on_background: Color
        get() = colour_scheme!!.onBackground
    override val accent: Color
        get() = colour_scheme!!.primary

    override fun toStaticThemeData(name: String): StaticThemeData =
        StaticThemeData(
            name,
            background,
            on_background,
            accent
        )
}
