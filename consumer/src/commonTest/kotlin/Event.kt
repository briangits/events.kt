import io.github.briangits.events.integration.metadata.Metadata

data class Event<T>(
    val data: T,
    val metadata: Metadata
)
