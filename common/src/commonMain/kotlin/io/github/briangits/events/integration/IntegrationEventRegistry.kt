package io.github.briangits.events.integration

/**
 * A registry for mapping event types to their definitions.
 *
 * @see EventType
 * @see IntegrationEventDefinition
 */
abstract class IntegrationEventRegistry {
    /**
     * The registered integration event definitions, keyed by their [EventType].
     */
    protected val events: MutableMap<EventType<*>, IntegrationEventDefinition<*>> = mutableMapOf()

    /**
     * Registers an event definition.
     *
     * @param T The type of the event data.
     * @param type The [EventType] to register.
     * @param definition The [IntegrationEventDefinition] to associate with the type.
     */
    fun <T : Any> register(
        type: EventType<T>,
        definition: IntegrationEventDefinition<T>
    ) {
        events[type] = definition
    }
}
