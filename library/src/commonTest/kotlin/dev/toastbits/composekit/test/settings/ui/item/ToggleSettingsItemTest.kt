package dev.toastbits.composekit.test.settings.ui.item

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import dev.mokkery.verify
import dev.toastbits.composekit.settings.ui.item.SettingsItem
import dev.toastbits.composekit.settings.ui.item.ToggleSettingsItem
import kotlin.test.Test
import kotlin.test.assertNotNull

class ToggleSettingsItemTest: SettingsItemTest() {

    @Test
    fun testToggleSettingsItem() = runComposeUiTest {
        val item: SettingsItem =
            ToggleSettingsItem(
                group.boolean_prop
            )

        var initial_value: Boolean? by mutableStateOf(null)

        setContent {
            LaunchedEffect(Unit) {
                initial_value = group.boolean_prop.get()
            }

            item.Item(
                settings_interface = settings_interface,
                openPage = { _, _ -> throw IllegalStateException() },
                openCustomPage = { throw IllegalStateException() },
                modifier = Modifier.testTag("settings_item")
            )
        }

        val item_node: SemanticsNodeInteraction = onNodeWithTag("settings_item")
        testGeneralSettingsItem(item_node)

        waitForIdle()
        assertNotNull(initial_value)

        val clickable_node: SemanticsNodeInteraction =
            onNode(
                SemanticsMatcher("") { node ->
                    node.config.any { it.key.name == "OnClick" }
                }
            )

        clickable_node.performClick()
        waitForIdle()

        clickable_node.performClick()
        waitForIdle()

        verify {
            editor.putBoolean(group.boolean_prop.key, !initial_value!!)
            editor.putBoolean(group.boolean_prop.key, initial_value!!)
        }
    }
}
