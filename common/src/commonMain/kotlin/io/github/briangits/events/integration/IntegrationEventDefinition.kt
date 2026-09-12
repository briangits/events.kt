package io.github.briangits.events.integration

/**
 * Defines the metadata required to publish or consume an event.
 *
 * @param T The type of the event payload.
 * @property name The unique name identifying the event in a [topic].
 * @property topic The topic where the event is published.
 * @property key A function that extracts the event key from the payload.
 */
class IntegrationEventDefinition<T : Any>(
    val name: String,
    val topic: String,
    val key: T.() -> Any?
)
