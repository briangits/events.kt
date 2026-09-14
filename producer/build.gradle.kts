import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)

    // Serialization
    alias(kotlinx.plugins.serialization)

    // Publishing
    id("io.github.briangits.events.integration.conventions.publishing")
}

kotlin {
    jvm()

    android {
        namespace = project.group.toString()
        compileSdk = 37
        minSdk = 21

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {
        commonMain.dependencies {
            api(projects.common)

            // Relay
            api(projects.relay.publisher)

            // Serialization
            api(projects.serialization.core)
            implementation(projects.serialization.json)

            // Metadata
            api(projects.metadata)

            // Coroutines
            implementation(kotlinx.coroutines)
        }

        commonTest.dependencies {
            implementation(kt.test)

            // Coroutines
            implementation(kotlinx.coroutines.test)
        }
    }
}

library {
    name = "producer"
    description = "Broker-agnostic publisher API for events.kt integration events"
}
