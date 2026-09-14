plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)

    // Publishing
    id("io.github.briangits.events.integration.conventions.publishing")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.serialization.core)

            api(kotlinx.serialization.json)
        }
    }
}

library {
    name = "serialization-json"
    description = "JSON serialization for events.kt integration events payloads"
}
