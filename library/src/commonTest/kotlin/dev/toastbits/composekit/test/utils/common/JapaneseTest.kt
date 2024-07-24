package dev.toastbits.composekit.test.utils.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import dev.toastbits.composekit.utils.common.*
import dev.toastbits.composekit.test.assertTrueWith
import dev.toastbits.composekit.test.assertFalseWith
import dev.toastbits.composekit.test.testReceiverCheck

class JapaneseTest {
    private val hiragana: Iterable<Char> = 'あ'..'ん'
    private val katakana: Iterable<Char> = 'ア'..'ン'
    private val kanji: Iterable<Char> = "一二三詩音寿司夜朝食紙".toList()
    private val latin: Iterable<Char> = 'a'..'z'

    @Test
    fun testCharisJa() {
        for (char in hiragana + katakana + kanji) {
            assertTrueWith(char) { isJa() }
        }
        for (char in latin) {
            assertFalseWith(char) { isJa() }
        }
    }

    @Test
    fun testCharisKanji() {
        for (char in kanji) {
            assertTrueWith(char) { isKanji() }
        }
        for (char in latin + hiragana + katakana) {
            assertFalseWith(char) { isKanji() }
        }
    }

    @Test
    fun testCharisHiragana() {
        for (char in hiragana) {
            assertTrueWith(char) { isHiragana() }
        }
        for (char in latin + kanji + katakana) {
            assertFalseWith(char) { isHiragana() }
        }
    }

    @Test
    fun testCharIsKatakana() {
        for (char in katakana) {
            assertTrueWith(char) { isKatakana() }
        }
        for (char in latin + kanji + hiragana) {
            assertFalseWith(char) { isKatakana() }
        }
    }

    @Test
    fun testCharIsFullWidth() {
        assertTrueWith('ア') { isFullWidth() }
        assertFalseWith('ｱ') { isFullWidth() }
    }

    @Test
    fun testCharToHiragana() {
        for ((hiragana, katakana) in hiragana.zip(katakana)) {
            assertNotEquals(hiragana, katakana)
            assertEquals(hiragana, katakana.toHiragana())
        }
    }

    @Test
    fun testStringHasKanjiAndHiraganaOrKatakana() {
        testReceiverCheck(String::hasKanjiAndHiraganaOrKatakana) {
            assertTrue("赤いカトレア")
            assertTrue("青い空")
            assertTrue("カタカナト漢字ダケフクム文章オモイツカヘン")
            assertFalse("漢字")
            assertFalse("ひらがなとカタカナ")
        }
    }
}
