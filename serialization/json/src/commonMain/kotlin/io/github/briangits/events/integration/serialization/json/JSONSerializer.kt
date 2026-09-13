package io.github.briangits.events.integration.serialization.json

import io.github.briangits.events.integration.serialization.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * A JSON format implementation of the [Serializer] interface using `kotlinx.serialization`.
 *
 * @property json The [Json] instance used for encoding and decoding.
 */
class JSONSerializer(val json: Json) : Serializer {
    override fun <T> serialize(value: T, serializer: KSerializer<T>): ByteArray =
        json.encodeToString(serializer, value).encodeToByteArray()

    override fun <T> deserialize(bytes: ByteArray, serializer: KSerializer<T>): T =
        json.decodeFromString(serializer, bytes.decodeToString())
}

fun json(block: () -> Json = { Json {} }): JSONSerializer = JSONSerializer(json = block())
/**
 * Creates a [JSONSerializer] using the provided configuration [block].
 *
 * @param block A configuration block for the [Json] instance.
 * Defaults to a default [Json] configuration.
 * @return A new [JSONSerializer] instance.
 */
