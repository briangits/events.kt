plugins {
     alias(kt.plugins.jvm)
    `java-gradle-plugin`
}

group = "io.github.briangits.events.integration.conventions"
version = "0.0.1"

gradlePlugin {
    plugins {
        create("publishing") {
            id = "$group.publishing"
            implementationClass = "$group.publishing.PublishPlugin"
        }
    }
}

fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) {
    val artifact =
        plugin.get().let {
            "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}"
        }

    implementation(artifact)
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    plugin(libutils.plugins.mavenPublish)
}
