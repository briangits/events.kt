package io.github.briangits.events.integration.consumer

import io.github.briangits.events.integration.metadata.Metadata

/**
 * Represents a consumed integration event.
 *
 * @param T The type of the event data.
 * @property data The deserialized event data.
 * @property metadata The [Metadata] associated with the event.
 */
data class Event<out T>(
    val data: T,
    val metadata: Metadata
)
