package io.github.briangits.events.integration.consumer

import io.github.briangits.events.integration.serialization.Serializer
import io.github.briangits.events.integration.serialization.json.JSONSerializer
import io.github.briangits.events.integration.serialization.json.json

/**
 * Configuration for an integration event [Consumer].
 *
 * @property serializer The [Serializer] used for decoding event data and metadata.
 *   Defaults to a [JSONSerializer].
 */
class ConsumerConfig(
    internal var serializer: Serializer = json(),
    block: ConsumerConfig.() -> Unit = {}
) {
    init { block() }

    /**
     * Configures the [Serializer] to be used by the consumer.
     *
     * @param block A factory function that returns a [Serializer] instance.
     */
    fun serialization(block: () -> Serializer) {
        serializer = block()
    }
}
