package io.github.briangits.events.integration.conventions.publishing

import org.gradle.api.provider.Property

interface LibraryExtension {
    val group: Property<String>
    val name: Property<String>
    val version: Property<String>

    val description: Property<String>
}
