package io.github.briangits.events.integration.producer

import io.github.briangits.events.integration.serialization.Serializer
import io.github.briangits.events.integration.serialization.json.JSONSerializer
import io.github.briangits.events.integration.serialization.json.json
import kotlinx.coroutines.CoroutineDispatcher

internal expect val defaultDispatcher: CoroutineDispatcher

/**
 * Configuration for an integration event [Producer].
 *
 * @property dispatcher The [CoroutineDispatcher] used for publishing events.
 *   Defaults to a platform-specific dispatcher (e.g., Dispatchers.IO on JVM).
 * @property serializer The [Serializer] used for encoding event data and metadata.
 *   Defaults to a [JSONSerializer].
 */
class ProducerConfig(
    var dispatcher: CoroutineDispatcher = defaultDispatcher,
    internal var serializer: Serializer = json(),
    block: ProducerConfig.() -> Unit = {}
) {
    init { block() }

    /**
     * Configures the [Serializer] to be used by the producer.
     *
     * @param block A factory function that returns a [Serializer] instance.
     */
    fun serialization(block: () -> Serializer) {
        serializer = block()
    }
}
