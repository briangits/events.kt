plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)

    // Publishing
    id("io.github.briangits.events.integration.conventions.publishing")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(kotlinx.serialization)
        }
    }
}

library {
    name = "serialization-core"
    description = "Serialization API for events.kt integration event payloads"
}
