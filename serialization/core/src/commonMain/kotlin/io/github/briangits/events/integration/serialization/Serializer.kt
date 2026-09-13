package io.github.briangits.events.integration.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * An interface for serializing and deserializing data to and from [ByteArray].
 *
 * Implementations of this interface define how objects are converted to bytes
 * and vice versa, typically using a specific format like JSON or Protobuf.
 */
interface Serializer {
    /**
     * Serializes the given [value] into a [ByteArray] using the provided [serializer].
     *
     * @param T The type of the value to serialize.
     * @param value The object to serialize.
     * @param serializer The [KSerializer] to use for serialization.
     * @return The serialized [ByteArray].
     */
    fun <T> serialize(value: T, serializer: KSerializer<T>): ByteArray

    /**
     * Deserializes the given [bytes] into an object of type [T] using the provided [serializer].
     *
     * @param T The type of the object to deserialize.
     * @param bytes The raw [ByteArray] to deserialize.
     * @param serializer The [KSerializer] to use for deserialization.
     * @return The deserialized object of type [T].
     */
    fun <T> deserialize(bytes: ByteArray, serializer: KSerializer<T>): T
}
