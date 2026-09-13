package io.github.briangits.events.integration.producer

import io.github.briangits.events.integration.EventType
import io.github.briangits.events.integration.IntegrationEventDefinition
import io.github.briangits.events.integration.IntegrationEventRegistry
import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.publisher.Publisher
import io.github.briangits.events.integration.eventType
import io.github.briangits.events.integration.metadata.Metadata
import kotlinx.coroutines.withContext
import kotlinx.serialization.serializer

/**
 * A producer for publishing integration events to a message broker.
 *
 * @property publisher The underlying [Publisher] implementation used to interact with the broker.
 * @param config An optional configuration block for the producer.
 */
class Producer(
    val publisher: Publisher,
    config: ProducerConfig.() -> Unit = {},
) : IntegrationEventRegistry() {
    private val config = ProducerConfig { config() }

    /**
     * Publishes an integration event of type [T].
     *
     * This method looks up the [IntegrationEventDefinition] for the given [type],
     * serializes the [data], and publishes it via the [publisher].
     *
     * @param T The type of the integration event.
     * @param data The event data to publish.
     * @param type The [EventType] of the event to publish.
     * @param block A configuration block for adding custom [Metadata] to the event.
     * @throws IllegalStateException If no event definition is found for the given [type].
     */
    suspend fun <T : Any> publish(
        data: T,
        type: EventType<T>,
        block: Metadata.() -> Unit
    ) = withContext(config.dispatcher) {
        @Suppress("UNCHECKED_CAST")
        val definition = events[type] as? IntegrationEventDefinition<T>
            ?: error("No event definition found for ${type.eventClass.simpleName}")

        val metadata = Metadata(config.serializer).apply {
            block()

            "eventName" to definition.name
        }

        val message = Message(
            route = Route(topic = definition.topic),
            key = definition.key(data)?.toString(),
            metadata = metadata.entries,
            data = config.serializer.serialize(data, serializer(type.type))
        )

        publisher.publish(message)
    }
}

/**
 * Publishes an integration event of type [T], deriving the [EventType] from the reified type [T].
 *
 * @param T The type of the integration event.
 * @param data The event data to publish.
 * @param block An optional configuration block for adding custom [Metadata] to the event.
 */
suspend inline fun <reified T : Any> Producer.publish(
    data: T,
    noinline block: Metadata.() -> Unit = {}
) = publish(data, type = eventType<T>(), block)

/**
 * Publishes an integration event of type [T] resolved by the [block].
 *
 * @param T The type of the integration event.
 * @param block A function that resolves the event data.
 */
suspend inline fun <reified T : Any> Producer.publish(block: () -> T) = publish(data = block())
