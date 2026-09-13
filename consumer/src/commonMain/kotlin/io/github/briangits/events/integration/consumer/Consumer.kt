package io.github.briangits.events.integration.consumer

import io.github.briangits.events.integration.EventType
import io.github.briangits.events.integration.IntegrationEventDefinition
import io.github.briangits.events.integration.IntegrationEventRegistry
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.consumer.MessageConsumer
import io.github.briangits.events.integration.eventType
import io.github.briangits.events.integration.metadata.Metadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer

/**
 * An integration events consumer.
 *
 * @property consumer The underlying [MessageConsumer] used to interact with the broker.
 * @param config An optional configuration block for the consumer.
 */
class Consumer(
    val consumer: MessageConsumer,
    config: ConsumerConfig.() -> Unit = {}
) : IntegrationEventRegistry() {
    private val config = ConsumerConfig { config() }

    /**
     * Consumes integration events of a specific type.
     *
     * This method looks up the [IntegrationEventDefinition] for the given [type],
     * filters messages from the [consumer] based on the event name, and deserializes
     * the message data into an [Event] object.
     *
     * @param T The type of the integration event.
     * @param type The [EventType] of the event.
     * @return A [Flow] of [Event] objects containing the deserialized data and metadata.
     * @throws IllegalStateException If no event definition is found for the given [type].
     */
    suspend fun <T : Any> consume(type: EventType<T>): Flow<Event<T>> {
        @Suppress("UNCHECKED_CAST")
        val definition = events[type] as? IntegrationEventDefinition<T>
            ?: error("No event definition found for ${type.eventClass.simpleName}")

        return consumer.consume(route = Route(topic = definition.topic))
            .filter {
                val name = it.metadata["eventName"]?.let {
                    config.serializer.deserialize(it, serializer<String>())
                }

                name == definition.name
            }.map {
                @Suppress("UNCHECKED_CAST")
                Event(
                    data = config.serializer.deserialize(it.data, serializer(type.type)) as T,
                    metadata = Metadata(config.serializer, it.metadata)
                )
            }
    }
}

/**
 * Subscribes to integration events of type [T].
 *
 * This is a convenience method that launches a coroutine to [consume] events of type [T]
 * and invokes the provided [block] for each event.
 *
 * @param T The type of the integration event.
 * @param scope An optional [CoroutineScope] to launch the subscription in. If not provided, 
 *   the current coroutine context scope is used.
 * @param block A callback function to handle received events.
 * @return A [Job] representing the subscription's coroutine.
 */
suspend inline fun <reified T : Any> Consumer.subscribe(
    scope: CoroutineScope? = null,
    crossinline block: suspend (data: T, metadata: Metadata) -> Unit
): Job {
    val subscriptionScope = scope ?: CoroutineScope(currentCoroutineContext())

    return subscriptionScope.launch(start = CoroutineStart.UNDISPATCHED) {
        consume(type = eventType<T>()).collect {
            block(it.data, it.metadata)
        }
    }
}
