plugins {
     alias(kt.plugins.jvm)
    `java-gradle-plugin`
}

group = "io.github.briangits.events.integration.conventions"
version = "0.0.1"

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
}
