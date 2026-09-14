plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)

    // Publishing
    id("io.github.briangits.events.integration.conventions.publishing")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.relay.common)
            api(projects.serialization.core)
        }
    }
}

library {
    name = "relay-publisher"
    description = "Message publisher API for events.kt relays"
}
