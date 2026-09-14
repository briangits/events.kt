plugins {
    alias(kt.plugins.multiplatform)

    // Publishing
    id("io.github.briangits.events.integration.conventions.publishing")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.kafka.common)
            api(projects.relay.publisher)

            implementation(kotlinx.coroutines)
        }

        commonTest.dependencies {
            implementation(kt.test)

            // Coroutines
            implementation(kotlinx.coroutines.test)
        }

        jvmMain.dependencies {
            api(kafka.clients)
        }

        jvmTest.dependencies {
            implementation(testcontainers.core)
            implementation(testcontainers.kafka)
        }
    }
}

library {
    name = "kafka-producer"
    description = "Kafka message publisher implementation for events.kt"
}
