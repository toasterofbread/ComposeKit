package dev.toastbits.composekit.settings.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.toastbits.composekit.settings.ui.item.GroupSettingsItem
import dev.toastbits.composekit.settings.ui.item.SettingsItem
import dev.toastbits.composekit.utils.composable.WidthShrinkText
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

abstract class SettingsPage {
    var id: Int? = null
        internal set
    lateinit var settings_interface: SettingsInterface

    open val scrolling: Boolean
        @Composable
        get() = true

    open val title: String?
        @Composable
        get() = null
    open val icon: ImageVector?
        @Composable
        get() = null

    @Composable
    fun Page(content_padding: PaddingValues, openPage: (Int, Any?) -> Unit, openCustomPage: (SettingsPage) -> Unit, goBack: () -> Unit) {
        CompositionLocalProvider(LocalContentColor provides settings_interface.theme.on_background) {
            PageView(content_padding, openPage, openCustomPage, goBack)
        }
    }

    @Composable
    fun TitleBar(is_root: Boolean, modifier: Modifier = Modifier) {
        TitleBar(is_root, modifier, null)
    }

    @Composable
    open fun TitleBar(is_root: Boolean, modifier: Modifier, titleFooter: (@Composable () -> Unit)?) {
        Crossfade(title, modifier) { title ->
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    val ic = icon
                    if (ic != null) {
                        Icon(ic, null)
                    }
                    else {
                        Spacer(Modifier.width(24.dp))
                    }

                    if (title != null) {
                        WidthShrinkText(
                            title,
                            Modifier.padding(horizontal = 30.dp).weight(1f),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = settings_interface.theme.on_background,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    TitleBarEndContent()
                }

                titleFooter?.invoke()
            }
        }
    }

    @Composable
    open fun TitleBarEndContent() {
        val coroutine_scope: CoroutineScope = rememberCoroutineScope()

        IconButton({
            coroutine_scope.launch {
                resetKeys()
            }
        }) {
            Icon(Icons.Default.Refresh, null)
        }
    }

    @Composable
    protected abstract fun PageView(content_padding: PaddingValues, openPage: (Int, Any?) -> Unit, openCustomPage: (SettingsPage) -> Unit, goBack: () -> Unit)

    abstract suspend fun resetKeys()
    open fun onClosed() {}
}

private const val SETTINGS_PAGE_WITH_ITEMS_SPACING = 20f

open class SettingsPageWithItems(
    val getTitle: () -> String?,
    val getItems: () -> List<SettingsItem>,
    val modifier: Modifier = Modifier,
    val getIcon: (@Composable () -> ImageVector?)? = null
): SettingsPage() {

    override val title: String?
        @Composable
        get() = getTitle()
    override val icon: ImageVector?
        @Composable
        get() = getIcon?.invoke()

    @Composable
    override fun PageView(
        content_padding: PaddingValues,
        openPage: (Int, Any?) -> Unit,
        openCustomPage: (SettingsPage) -> Unit,
        goBack: () -> Unit
    ) {
        Crossfade(getItems()) { items ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(SETTINGS_PAGE_WITH_ITEMS_SPACING.dp),
                contentPadding = content_padding
            ) {
                items(items.size) { i ->
                    val item = items[i]

                    if (i != 0 && item is GroupSettingsItem) {
                        Spacer(Modifier.height(30.dp))
                    }

                    item.Item(settings_interface, openPage, openCustomPage, Modifier)
                }
            }
        }
    }

    override suspend fun resetKeys() {
        for (item in getItems()) {
            item.resetValues()
        }
    }
}
