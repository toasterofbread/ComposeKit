package dev.toastbits.composekit.test.settings.ui.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.invokeGlobalAssertions
import androidx.compose.ui.test.onAllNodesWithText
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.PreferencesProperty
import dev.toastbits.composekit.settings.ui.SettingsInterface
import dev.toastbits.composekit.settings.ui.SettingsPage
import dev.toastbits.composekit.settings.ui.theme.ThemeValuesData
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

abstract class SettingsItemTest {
    protected lateinit var prefs: PlatformPreferences
    protected lateinit var editor: PlatformPreferences.Editor
    protected lateinit var group: TestPreferencesGroup
    protected lateinit var settings_interface: SettingsInterface

    private val property_name: String = "Property name"
    private val property_description: String = "Property description"

    protected inner class TestPreferencesGroup(prefs: PlatformPreferences): PreferencesGroup("test", prefs) {
        val boolean_prop: PreferencesProperty<Boolean> by
            property(
                getName = { property_name },
                getDescription = { property_description },
                getDefaultValue = { false }
            )
    }

    private var item_tested: Boolean = false

    protected fun ComposeUiTest.testGeneralSettingsItem(node: SemanticsNodeInteraction) {
        node.assertExists()

        // Text might be duplicated due to WidthShrinkText
        onAllNodesWithText(property_name).assertAny(SemanticsMatcher("") { true })
        onAllNodesWithText(property_description).assertAny(SemanticsMatcher("") { true })

        item_tested = true
    }

    @BeforeTest
    fun beforeTest() {
        item_tested = false
        val reference_group: TestPreferencesGroup = TestPreferencesGroup(mock())

        editor = mock {
            every { putBoolean(any(), any()) } calls { editor }
        }
        prefs = mock {
            every { getBoolean(reference_group.boolean_prop.key, any()) } returns false
            every { addListener(any()) } returns mock()
            every { removeListener(any()) } returns Unit
            @Suppress("UNCHECKED_CAST")
            every { edit(any()) } calls { (it.args.single() as (PlatformPreferences.Editor) -> Unit).invoke(editor) }
        }
        group = TestPreferencesGroup(prefs)
        settings_interface =
            SettingsInterface(
                context = mock(MockMode.autofill) {},
                themeProvider = { ThemeValuesData.singleColour(Color.White) },
                root_page = 0,
                prefs = prefs,
                triggerVibration = {},
                getPage = { _, _ ->
                    object : SettingsPage() {
                        @Composable
                        override fun PageView(
                            content_padding: PaddingValues,
                            openPage: (Int, Any?) -> Unit,
                            openCustomPage: (SettingsPage) -> Unit,
                            goBack: () -> Unit
                        ) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun resetKeys() {
                            TODO("Not yet implemented")
                        }
                    }
                }
            )
    }

    @AfterTest
    fun afterTest() {
        assertTrue(item_tested, "testGeneralSettingsItem was not called")
    }
}
