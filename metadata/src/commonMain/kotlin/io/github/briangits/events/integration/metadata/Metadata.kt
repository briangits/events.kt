package io.github.briangits.events.integration.metadata

import io.github.briangits.events.integration.serialization.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Represents metadata associated with an integration event.
 *
 * Metadata is stored as a map of [String] keys to [ByteArray] values, and uses a [Serializer]
 * to handle the conversion of specific types to and from bytes. It also provides a caching
 * mechanism for deserialized values.
 *
 * @property serializer The [Serializer] used for encoding and decoding metadata values.
 */
class Metadata(private val serializer: Serializer) {

    /**
     * Creates a new [Metadata] instance and applies the provided [block] for configuration.
     *
     * Example:
     * ```
     * val metadata = Metadata(serializer) {
     *     "correlationId" to "123-abc"
     *     "timestamp" to Clock.System.now()
     * }
     * ```
     *
     * @param serializer The [Serializer] used for encoding and decoding metadata values.
     * @param block A configuration block executed on the new [Metadata] instance.
     */
    constructor(serializer: Serializer, block: Metadata.() -> Unit) : this(serializer) { block() }

    /**
     * Creates a new [Metadata] instance with the given [Serializer] and initial [entries].
     *
     * @param serializer The [Serializer] used for encoding and decoding metadata values.
     * @param entries Initial metadata entries.
     */
    constructor(serializer: Serializer, entries: Map<String, ByteArray>) : this(serializer) {
        this.entries.putAll(entries)
    }

    /**
     * The underlying raw metadata entries as byte arrays.
     */
    val entries: Map<String, ByteArray>
    field = mutableMapOf<String, ByteArray>()

    private val cached = mutableMapOf<String, Any?>()

    /**
     * Retrieves the value associated with the given [key],
     * deserializing it using the provided [serializer].
     *
     * @param T The type of the value to retrieve.
     * @param key The metadata key.
     * @param serializer The [KSerializer] for the type [T].
     * @return The deserialized value, or `null` if the key is not present.
     */
    operator fun <T> get(key: String, serializer: KSerializer<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return cached.getOrPut(key) {
            entries[key]?.let { this.serializer.deserialize(it, serializer) }
        } as T
    }

    /**
     * Retrieves the value associated with the given [key],
     * deriving the serializer from the reified type [T].
     *
     * @param T The reified type of the value to retrieve.
     * @param key The metadata key.
     * @return The deserialized value, or `null` if the key is not present.
     */
    inline operator fun <reified T> get(key: String): T? = get(key, serializer<T>())

    /**
     * Sets the value associated with the given [key],
     * serializing it using the provided [serializer].
     *
     * @param T The type of the value to set.
     * @param key The metadata key.
     * @param value The value to store.
     * @param serializer The [KSerializer] for the type [T].
     */
    operator fun <T> set(key: String, value: T, serializer: KSerializer<T>) {
        entries.set(key, this.serializer.serialize(value, serializer))
            .also { cached[key] = value }
    }

    /**
     * Sets the value associated with the given [key],
     * deriving the serializer from the reified type [T].
     *
     * @param T The type of the value to set.
     * @param key The metadata key.
     * @param value The value to store.
     */
    inline operator fun <reified T> set(key: String, value: T) = set(key, value, serializer<T>())

    /**
     * An infix function to set a metadata value using the `to` keyword.
     *
     * Example:
     * ```
     * Metadata(serializer) { "userId" to 123 }
     * ```
     */
    inline infix fun <reified T> String.to(value: T) = set(this, value)

    /**
     * Removes the metadata entry associated with the given [key].
     *
     * @param key The metadata key to clear.
     * @return The raw byte array that was removed, or `null` if the key was not found.
     */
    fun clear(key: String) = entries.remove(key).also { cached.remove(key) }
}
