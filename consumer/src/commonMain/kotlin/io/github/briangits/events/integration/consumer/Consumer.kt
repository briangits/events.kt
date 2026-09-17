package io.github.briangits.events.integration.consumer

import io.github.briangits.events.integration.EventType
import io.github.briangits.events.integration.IntegrationEventDefinition
import io.github.briangits.events.integration.IntegrationEventRegistry
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.consumer.MessageConsumer
import io.github.briangits.events.integration.eventType
import io.github.briangits.events.integration.metadata.Metadata
import io.github.briangits.events.integration.serialization.serialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer

/**
 * An integration events consumer.
 *
 * @property consumer The underlying [MessageConsumer] used to interact with the broker.
 * @param config An optional configuration block for the consumer.
 */
class Consumer(
    private val consumer: MessageConsumer,
    config: ConsumerConfig.() -> Unit = {}
) : IntegrationEventRegistry() {
    private val config = ConsumerConfig { config() }

    /**
     * Subscribes to integration events of the specified [type].
     *
     * @param T The type of the integration event.
     * @param type The [EventType] of the event.
     * @param scope An optional [CoroutineScope] to launch the subscription in.
     *   If not provided, the current coroutine context scope is used.
     * @return A [Job] representing the subscription's coroutine.
     * @throws IllegalArgumentException If no event definition exists for the given [type].
     */
    suspend fun <T : Any> subscribe(
        type: EventType<T>,
        scope: CoroutineScope? = null,
        block: suspend (data: T, metadata: Metadata) -> Unit
    ): Job {
        @Suppress("UNCHECKED_CAST")
        val definition = events[type] as? IntegrationEventDefinition<T>
        requireNotNull(definition) {
            "No event definition found for ${type.eventClass.simpleName}"
        }

        val eventName = config.serializer.serialize(definition.name)

        val subscriptionScope = scope ?: CoroutineScope(currentCoroutineContext())

        return subscriptionScope.launch(start = CoroutineStart.UNDISPATCHED) {
            consumer.subscribe(route = Route(topic = definition.topic)) {
                val name = it.metadata["eventName"]
                if (!name.contentEquals(eventName)) return@subscribe

                @Suppress("UNCHECKED_CAST")
                val event = config.serializer.deserialize(it.data, serializer(type.type)) as T
                val metadata = Metadata(config.serializer, it.metadata)

                block(event, metadata)
            }
        }
    }

    /**
     * Subscribes to integration events of type [T],
     * deriving the [EventType] from the reified type [T].
     *
     * @param T The type of the integration event.
     * @param scope An optional [CoroutineScope] to launch the subscription in.
     *   If not provided, the current coroutine context scope is used.
     * @param block A callback function to handle received events.
     * @return A [Job] representing the subscription's coroutine.
     * @throws IllegalArgumentException If no event definition exists for the type [T].
     */
    suspend inline fun <reified T : Any> subscribe(
        scope: CoroutineScope? = null,
        crossinline block: suspend (data: T, metadata: Metadata) -> Unit
    ): Job = subscribe(type = eventType<T>(), scope) { event, metadata ->
        block(event, metadata)
    }

    /**
     * Subscribes to integration events of type [T],
     * deriving the [EventType] from the reified type [T].
     *
     * @param T The type of the integration event.
     * @param scope An optional [CoroutineScope] to launch the subscription in. If not provided,
     *   the current coroutine context scope is used.
     * @param block A callback function to handle received events.
     * @return A [Job] representing the subscription's coroutine.
     * @throws IllegalArgumentException If no event definition exists for the type [T].
     */
    suspend inline fun <reified T : Any> subscribe(
        scope: CoroutineScope? = null,
        crossinline block: suspend (data: T) -> Unit
    ): Job = subscribe<T>(scope) { event, _ -> block(event) }
}
