package dev.toastbits.composekit.test.platform

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PreferencesGroup
import dev.toastbits.composekit.platform.PreferencesProperty
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.BeforeTest
import kotlin.test.Test

class PreferencesGroupTest {
    private lateinit var prefs: PlatformPreferences
    private lateinit var group: TestGroup
    private lateinit var editor: PlatformPreferences.Editor

    private enum class TestEnum {
        A, B;
    }

    @Serializable
    private data class TestData(val value: Int)

    private inner class TestGroup(prefs: PlatformPreferences): PreferencesGroup("test", prefs) {
        val string_prop: PreferencesProperty<String> by
            property(
                getName = { "" },
                getDescription = { null },
                getDefaultValue = { "" }
            )
        val enum_prop: PreferencesProperty<TestEnum> by
            enumProperty(
                getName = { "" },
                getDescription = { null },
                getDefaultValue = { TestEnum.A }
            )
        val data_prop: PreferencesProperty<TestData> by
            serialisableProperty(
                getName = { "" },
                getDescription = { null },
                getDefaultValue = { TestData(0) }
            )
    }

    @BeforeTest
    fun setup() {
        val reference_group: TestGroup = TestGroup(mock())

        editor = mock {
            every { putString(reference_group.string_prop.key, any()) } returns mock()
            every { putInt(reference_group.enum_prop.key, any()) } returns mock()
            every { putSerialisable<TestData>(reference_group.data_prop.key, any(), any()) } returns mock()
        }

        prefs = mock {
            @Suppress("UNCHECKED_CAST")
            every { edit(any()) } calls { (it.args.single() as PlatformPreferences.Editor.() -> Unit).invoke(editor) }
        }

        group = TestGroup(prefs)
    }

    @Test
    fun testStringProperty() = runTest  {
        group.string_prop.set("new value")
        verify {
            editor.putString(group.string_prop.key, "new value")
        }
    }

    @Test
    fun testEnumProperty() = runTest  {
        group.enum_prop.set(TestEnum.B)
        verify {
            editor.putInt(group.enum_prop.key, TestEnum.B.ordinal)
        }
    }

    @Test
    fun testDataProperty() = runTest  {
        group.data_prop.set(TestData(1))
        verify {
            editor.putSerialisable(group.data_prop.key, TestData(1), serializer())
        }
    }
}
