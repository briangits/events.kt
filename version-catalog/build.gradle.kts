plugins {
    `version-catalog`

    // Publishing
    id("io.github.briangits.events.integration.conventions.publishing")
}

fun VersionCatalogBuilder.library(
    group: String? = null,
    name: String,
    versionRef: String = "events"
) {
    var groupId = rootProject.group.toString()
    if (group != null) groupId += ".$group"

    library(name, groupId, name).versionRef(versionRef)
}

catalog {
    versionCatalog {
        // Version
        version("events", rootProject.version.toString())

        // Annotations
        library(name = "annotations")

        // Serialization
        library(group = "serialization", name = "serialization-core")
        library(group = "serialization", name = "serialization-json")

        // Relays
        library(group = "relay", name = "relay-publisher")
        library(group = "relay", name = "relay-consumer")

        // Producer
        library(name = "producer")

        // Consumer
        library(name = "consumer")

        // Kafka Implementations
        library(group = "relay.kafka", name = "kafka-publisher")
        library(group = "relay.kafka", name = "kafka-consumer")

        // Gradle Plugin
        plugin("integrationEvents", rootProject.group.toString())
            .versionRef("events")
    }
}

library {
    name = "version-catalog"
    description = "Version catalog for events.kt"
}
