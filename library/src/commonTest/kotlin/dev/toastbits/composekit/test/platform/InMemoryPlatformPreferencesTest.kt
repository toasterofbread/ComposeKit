package dev.toastbits.composekit.test.platform

import dev.mokkery.answering.calls
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.toastbits.composekit.platform.InMemoryPlatformPreferences
import dev.toastbits.composekit.platform.PlatformPreferences
import dev.toastbits.composekit.platform.PlatformPreferencesListener
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InMemoryPlatformPreferencesTest {
    private lateinit var prefs: PlatformPreferences
    private val test_keys: Iterable<Int> = 1..10

    @Serializable
    private data class TestData(val value: Int)

    @BeforeTest
    fun setup() {
        prefs = InMemoryPlatformPreferences()
    }

    @Test
    fun testInitialState() {
        for (key in test_keys) {
            assertFalse(prefs.contains(key.toString()))
        }
    }

    @Test
    fun testSetGet() {
        prefs.edit {
            for (key in test_keys) {
                assertFalse(prefs.contains(key.toString()))
                putSerialisable(key.toString(), TestData(key), serializer())
            }
        }

        for (key in test_keys) {
            assertTrue(prefs.contains(key.toString()))
            assertEquals(TestData(key), prefs.getSerialisable(key.toString(), TestData(-1), serializer()))
        }

        prefs.edit {
            for (key in test_keys) {
                remove(key.toString())
            }
        }

        for (key in test_keys) {
            assertFalse(prefs.contains(key.toString()))
        }
    }

    @Test
    fun testListener() {
        val key: String = "test key"
        val listener: PlatformPreferencesListener =
            mock {
                every { onChanged(prefs, key) } calls {}
            }

        prefs.addListener(listener)

        prefs.edit {
            putInt(key, 0)
        }

        verify {
            listener.onChanged(prefs, key)
        }
    }
}
