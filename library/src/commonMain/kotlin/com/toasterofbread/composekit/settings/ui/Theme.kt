package dev.toastbits.composekit.settings.ui

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.utils.common.compare
import dev.toastbits.composekit.utils.common.contrastAgainst
import dev.toastbits.composekit.utils.common.getContrasted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.catppuccin.Palette as Catppuccin

const val VIBRANT_ACCENT_CONTRAST: Float = 0.2f

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class Theme(
    system_theme_default_name: String,
): ThemeData {
    enum class Colour {
        BACKGROUND,
        CARD,
        ACCENT,
        VIBRANT_ACCENT;

        fun get(theme: Theme): Color =
            when (this) {
                BACKGROUND -> theme.background
                CARD -> theme.card
                ACCENT -> theme.accent
                VIBRANT_ACCENT -> theme.vibrant_accent
            }
    }

    private lateinit var coroutine_scope: CoroutineScope
    val preview_active: Boolean get() = preview_theme_data != null
    override val name: String get() = getCurrentTheme().name

    override val background: Color get() = background_state.value
    override val on_background: Color get() = on_background_state.value
    override val card: Color get() = card_state.value
    override val accent: Color get() = accent_state.value
    val on_accent: Color get() = accent.getContrasted()
    val vibrant_accent: Color get() = makeVibrant(accent)

    private var current_theme_idx: Int = 0
    fun setCurrentThemeIdx(idx: Int, update_colours: Boolean = true) {
        current_theme_idx = idx
        if (update_colours) {
            updateColourValues()
        }
    }

    abstract fun saveThemes(themes: List<ThemeData>)
    abstract fun loadThemes(): List<ThemeData>

    abstract fun selectAccentColour(theme_data: ThemeData, thumbnail_colour: Color?): Color
    abstract fun getDarkColorScheme(): ColorScheme
    abstract fun getLightColorScheme(): ColorScheme

    fun makeVibrant(colour: Color, against: Color = background): Color {
        if (colour.compare(background) > 0.8f) {
            return colour.contrastAgainst(against, VIBRANT_ACCENT_CONTRAST, clip = false)
        }
        return colour
    }

    fun getCurrentTheme(): ThemeData {
        preview_theme_data?.also {
            return it
        }

        val themes: List<ThemeData> = getLoadedThemes()
        if ((current_theme_idx - 1) !in themes.indices) {
            return getCurrentSystemTheme()
        }
        else {
            return themes[current_theme_idx - 1]
        }
    }

    private fun getCurrentSystemTheme(): ThemeData {
        val gtk_theme: String? = System.getenv("GTK_THEME")?.lowercase()

        if (gtk_theme?.startsWith("catppuccin-") == true) {
            val split: List<String> = gtk_theme.substring(11).split("-", limit = 4)
            if (split.size >= 3) {
                val flavour: String = split[0]
                val accent: String = split[2]

                val theme: ThemeData? = getCatppuccinTheme(flavour, accent)
                if (theme != null) {
                    return theme.toStaticThemeData(default_system_theme.name + ": " + theme.name)
                }
            }
        }

        return default_system_theme
    }

    private val default_themes: List<ThemeData> = getDefaultThemes()
    private val default_system_theme: ColourSchemeThemeData = ColourSchemeThemeData(system_theme_default_name)
    private var loaded_themes: List<ThemeData>? by mutableStateOf(null)

    private var preview_theme_data: ThemeData? by mutableStateOf(null)
    private var thumbnail_colour: Color? = null

    private val background_state: Animatable<Color, AnimationVector4D> by lazy { Animatable(getLoadedThemes().first().background) }
    private val on_background_state: Animatable<Color, AnimationVector4D> by lazy { Animatable(getLoadedThemes().first().on_background) }
    private val card_state: Animatable<Color, AnimationVector4D> by lazy { Animatable(getLoadedThemes().first().card) }
    private val accent_state: Animatable<Color, AnimationVector4D> by lazy { Animatable(getLoadedThemes().first().accent) }

    private fun getLoadedThemes(): List<ThemeData> {
        if (loaded_themes == null) {
            loaded_themes = loadThemes().ifEmpty { default_themes }
        }
        assert(!loaded_themes.isNullOrEmpty())
        return loaded_themes!!
    }

    override fun toStaticThemeData(name: String): StaticThemeData =
        StaticThemeData(
            name,
            background_state.targetValue,
            on_background_state.targetValue,
            card_state.targetValue,
            accent_state.targetValue
        )

    override fun serialise(): String = toStaticThemeData(name).serialise()

    fun getThemes(): List<ThemeData> {
        return listOf(getCurrentSystemTheme()) + getLoadedThemes()
    }
    fun getThemeCount(): Int = getLoadedThemes().size

    open fun onAccentColourChanged(colour: Color) {}

    private fun updateColourValues() {
        with(coroutine_scope) {
            val data = getCurrentTheme()
            launch {
                background_state.animateTo(data.background)
            }
            launch {
                on_background_state.animateTo(data.on_background)
            }
            launch {
                card_state.animateTo(data.card)
            }
            launch {
                accent_state.animateTo(selectAccentColour(data, thumbnail_colour))
            }
        }
    }

    @Composable
    fun Update() {
        coroutine_scope = rememberCoroutineScope()

        val dark_theme: Boolean = isSystemInDarkTheme()
        LaunchedEffect(dark_theme) {
            default_system_theme.colour_scheme = if (dark_theme) getDarkColorScheme() else getLightColorScheme()
            onAccentColourChanged(selectAccentColour(getCurrentTheme(), thumbnail_colour))
            updateColourValues()
        }
    }

    fun setPreviewThemeData(preview_data: ThemeData?) {
        preview_theme_data = preview_data
        updateColourValues()
    }

    open fun currentThumbnnailColourChanged(new_colour: Color?, snap: Boolean = false) {
        thumbnail_colour = new_colour

        coroutine_scope.launch {
            val accent = selectAccentColour(getCurrentTheme(), thumbnail_colour)
            onAccentColourChanged(accent)

            if (snap) {
                accent_state.snapTo(accent)
            }
            else {
                accent_state.animateTo(accent)
            }
        }
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
        var themes: List<ThemeData> = getLoadedThemes()
        if (index <= 0 || themes.size == 1) {
            themes = default_themes
        }
        else {
            themes = themes.toMutableList().also { it.removeAt(index - 1) }
        }

        loaded_themes = themes
        saveThemes(themes)
    }

    fun reloadThemes() {
        loaded_themes = loadThemes().ifEmpty { default_themes }
        updateColourValues()
    }

    private fun getCatppuccinTheme(target_flavour: String, target_accent: String): ThemeData? {
        for (flavour in Catppuccin.toList()) {
            if (flavour.name == target_flavour) {
                for (accent in flavour.toList()) {
                    if (accent.key == target_accent) {
                        return StaticThemeData(
                            "Catppuccin ${flavour.name.replaceFirstChar { it.uppercaseChar() }} (${accent.key})",
                            background = Color(flavour.base.rgb),
                            on_background = Color(flavour.text.rgb),
                            card = Color(flavour.mantle.rgb),
                            accent = Color(accent.value.rgb)
                        )
                    }
                }
                break
            }
        }

        return null
    }

    fun getDefaultThemes(): List<ThemeData> =
        listOf(
            "mauve",
            "lavender",
            "red",
            "yellow",
            "green",
            "teal",
            "pink",
            "sapphire",
            "rosewater",
            "peach",
            "sky",
            "maroon",
            "blue",
            "flamingo"
        ).mapNotNull { accent ->
            getCatppuccinTheme("mocha", accent)
        }
}

interface ThemeData {
    val name: String
    val background: Color
    val on_background: Color
    val card: Color
    val accent: Color

    fun isEditable(): Boolean = false
    fun serialise(): String
    fun toStaticThemeData(name: String): StaticThemeData
}

data class StaticThemeData(
    override val name: String,
    override val background: Color,
    override val on_background: Color,
    override val card: Color,
    override val accent: Color
): ThemeData {
    override fun serialise(): String =
        "${background.toArgb()},${on_background.toArgb()},${card.toArgb()},${accent.toArgb()},$name"

    override fun isEditable(): Boolean = true
    override fun toStaticThemeData(name: String): StaticThemeData = copy(name = name)

    companion object {
        fun deserialise(data: String): StaticThemeData {
            val split = data.split(',', limit = 5)
            return StaticThemeData(
                split[4],
                Color(split[0].toInt()),
                Color(split[1].toInt()),
                Color(split[2].toInt()),
                Color(split[3].toInt())
            )
        }
    }
}

class ColourSchemeThemeData(
    override val name: String
): ThemeData {
    var colour_scheme: ColorScheme? by mutableStateOf(null)

    override val background: Color
        get() = colour_scheme?.background ?: Color.Unspecified
    override val on_background: Color
        get() = colour_scheme?.onBackground ?: Color.Unspecified
    override val card: Color
        get() = colour_scheme?.surfaceColorAtElevation(2.dp) ?: Color.Unspecified
    override val accent: Color
        get() = colour_scheme?.primary ?: Color.Unspecified

    override fun toStaticThemeData(name: String): StaticThemeData =
        StaticThemeData(
            name,
            background,
            on_background,
            card,
            accent
        )

    override fun serialise() = toStaticThemeData(name).serialise()
}
