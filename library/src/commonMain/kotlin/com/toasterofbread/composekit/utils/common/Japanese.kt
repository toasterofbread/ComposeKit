package dev.toastbits.composekit.utils.common

fun Char.isJa(): Boolean = isKanji() || isHiragana() || isKatakana()

fun Char.isKanji(): Boolean =
    ('\u4e00' <= this) && (this <= '\u9faf')

fun Char.isHiragana(): Boolean =
    ('\u3040' <= this) && (this <= '\u309F')

fun Char.isKatakana(): Boolean =
    ('\u30A0' <= this) && (this <= '\u30FF')

// http://kevin3sei.blog95.fc2.com/blog-entry-111.html
fun Char.isFullWidth(): Boolean =
    !(this <= '\u007e' || this == '\u00a5' || this == '\u203e' || this in '\uff61'..'\uff9f')

fun Char.isHalfWidthKatakana(): Boolean {
    return ('\uff66' <= this) && (this <= '\uff9d')
}

fun Char.isFullWidthKatakana(): Boolean {
    return ('\u30a1' <= this) && (this <= '\u30fe')
}

fun Char.toHiragana(): Char {
    if (isFullWidthKatakana()) {
        return (this - 0x60)
    }
    else if (isHalfWidthKatakana()) {
        return (this - 0xcf25)
    }
    return this
}

fun String.toHiragana(): String {
    val ret = StringBuilder()
    for (char in this) {
        ret.append(char.toHiragana())
    }
    return ret.toString()
}

fun String.hasKanjiAndHiraganaOrKatakana(): Boolean {
    var has_kanji = false
    var has_hiragana_or_katakana = false
    for (char in this) {
        when {
            char.isKanji() -> {
                if (has_hiragana_or_katakana)
                    return true
                has_kanji = true
            }
            char.isHiragana() || char.isKatakana() -> {
                if (has_kanji)
                    return true
                has_hiragana_or_katakana = true
            }
            else -> {}
        }
    }
    return false
}
