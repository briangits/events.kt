package io.github.briangits.events.integration

/**
 * Marks a class as an integration event.
 *
 * Annotated classes must also be marked as `@Serializable`
 *
 * @property topic The topic the event is published to.
 * @property name The unique name of the event in the topic.
 *   Defaults to the class name if not specified.
 * @property key The property used to extract the event key. Supports nested properties.
 *
 * Example:
 * ```
 * @IntegrationEvent("users", name = "created", key = "user.id")
 * data class UserCreated(val user: User, val createdAt: Instant)
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntegrationEvent(
    val topic: String,
    val name: String = "",
    val key: String = ""
)
