package io.github.briangits.events.integration.conventions.publishing

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

class PublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("com.vanniktech.maven.publish")

            val library = project.extensions.create<LibraryExtension>("library").apply {
                group.convention(project.group.toString())
                name.convention(project.name)
                version.convention(project.version.toString())

                description.convention("")
            }

            afterEvaluate {
                val artifact = object {
                    val group = library.group.get()
                    val name = library.name.get()
                    val version = library.version.get()

                    val description = library.description.get()
                }

                project.extensions.configure<MavenPublishBaseExtension> {
                    publishToMavenCentral(automaticRelease = true)
                    signAllPublications()

                    coordinates(artifact.group, artifact.name, artifact.version)

                    pom {
                        with(it) {
                            name.set(artifact.name)
                            description.set(artifact.description)
                            inceptionYear.set("2026")
                            url.set("https://github.com/briangits/events.kt")

                            licenses {
                                it.license {
                                    it.name.set("The Apache License, Version 2.0")
                                    it.url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                    it.distribution.set("repo")
                                }
                            }

                            developers {
                                it.developer {
                                    it.id.set("briangits")
                                    it.name.set("Gits")
                                    it.url.set("https://github.com/briangits")
                                }
                            }

                            scm {
                                val url = "https://github.com/briangits/events.kt"

                                it.url.set(url)
                                it.connection.set("scm:git$url.git")
                                it.developerConnection.set("scm:git:$url.git")
                            }
                        }
                    }
                }
            }
        }
    }
}
