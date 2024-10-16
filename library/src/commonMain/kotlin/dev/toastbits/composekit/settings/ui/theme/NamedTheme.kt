package dev.toastbits.composekit.settings.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.serializer

@Serializable(NamedThemeSerialiser::class)
data class NamedTheme(val name: String, val theme: ThemeValuesData)

@Serializable
private data class NativeNamedTheme(val name: String, val theme: ThemeValuesData)

@OptIn(InternalSerializationApi::class)
object NamedThemeSerialiser: KSerializer<NamedTheme> {
    private val DEFAULT_ERROR_COLOUR: Color = Color.Red

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NamedTheme", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): NamedTheme {
        if (decoder !is JsonDecoder) {
            throw UnsupportedOperationException("Decoder is ${decoder::class}, but expected JsonDecoder")
        }

        val element: JsonElement = decoder.decodeJsonElement()
        when (element) {
            is JsonObject -> {
                return Json.decodeFromJsonElement<NativeNamedTheme>(element).run { NamedTheme(name, theme) }
            }
            is JsonPrimitive -> {
                val data: String = element.content

                val split: List<String> = data.split(',', limit = 5)
                check(split.size == 5) { "data.split(',') size is not 5 (${split.size}): $split" }

                return NamedTheme(
                    split[4],
                    ThemeValuesData(
                        split[0].toInt(),
                        split[1].toInt(),
                        split[2].toInt(),
                        split[3].toInt(),
                        DEFAULT_ERROR_COLOUR.toArgb()
                    )
                )
            }
            else -> throw UnsupportedOperationException("Expected object or string (old format), got ${element::class}")
        }
    }

    override fun serialize(encoder: Encoder, value: NamedTheme) {
        with(value) {
            encoder.encodeSerializableValue(
                NativeNamedTheme::class.serializer(),
                NativeNamedTheme(name, theme)
            )
        }
    }
}
