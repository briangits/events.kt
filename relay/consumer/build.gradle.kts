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

            implementation(kotlinx.coroutines)
        }
    }
}

library {
    name = "relay-consumer"
    description = "Message consumer API for events.kt relays"
}
