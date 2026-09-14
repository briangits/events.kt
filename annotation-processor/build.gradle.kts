plugins {
    alias(kt.plugins.jvm)

    // Publishing
    id("io.github.briangits.events.integration.conventions.publishing")
}

library {
    name = "annotation-processor"
    description = "KSP processor for generating events.kt integration event definitions"
}

dependencies {
    implementation(projects.common)
    implementation(projects.annotations)

    implementation(codegen.ksp)

    implementation(codegen.poet)
    implementation(codegen.poet.ksp)

    implementation(kotlinx.serialization)
}
