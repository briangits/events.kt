package io.github.briangits.events.integration

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A type-safe representation of an event's type information.
 *
 * @param T The type of the event.
 * @property eventClass The [KClass] of [T].
 * @property type The [KType] of [T].
 */
data class EventType<out T : Any>(
    val eventClass: KClass<out T>,
    val type: KType
)

/**
 * Creates an [EventType] representing the type [T].
 *
 * @param T The event type.
 * @return An [EventType] representing [T].
 */
inline fun <reified T : Any> eventType(): EventType<T> =
    EventType(eventClass = T::class, type =  typeOf<T>())
