@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `jvm-test-suite`
    embeddedKotlin("jvm")
    embeddedKotlin("plugin.serialization")
    alias(libs.plugins.plugin.publish)
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_8
        languageVersion = KotlinVersion.KOTLIN_1_8
        jvmTarget = JvmTarget.JVM_1_8
        javaParameters = true
        freeCompilerArgs.addAll(
            "-Xlambdas=indy",
            "-Xjvm-default=all",
            "-Xemit-jvm-type-annotations",
            "-Xnew-inference",
            "-Xassertions=jvm"
        )
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(embeddedKotlin("stdlib"))
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.encoding)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.apache5)

    compileOnly(libs.annotations)
}

gradlePlugin {
    website = "https://github.com/Prototik/CursedPublish"
    vcsUrl = "https://github.com/Prototik/CursedPublish"
    plugins {
        register("cursed-publish") {
            id = "rocks.aur.cursed.publish"
            implementationClass = "rocks.aur.cursed.publish.CursedPublishPlugin"
            description = "Plugin to publish artifacts to the CurseForge portal"
            displayName = "Cursed Publish plugin"
            tags.addAll("curseforge", "publish", "publication", "upload", "release")
        }
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                implementation(libs.ktor.client.mock)
                implementation.bundle(libs.bundles.kotest)
            }

            targets.configureEach {
                testTask {
                    systemProperty(
                        "kotest.framework.parallelism",
                        Runtime.getRuntime().availableProcessors().coerceAtLeast(2)
                    )
                    systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "true")
                    systemProperty("kotest.framework.classpath.scanning.config.disable", "true")
                    systemProperty("kotest.framework.config.fqn", "rocks.aur.cursed.publish.test.KotestConfig")

                    jvmArgumentProviders += CommandLineArgumentProvider {
                        listOf(
                            "-Dgradle.build.dir=${project.layout.buildDirectory.get().asFile}",
                            "-Dgradle.test.suite.name=${this@getting.name}"
                        )
                    }

                    reports {
                        junitXml.required = false
                        html.required = false
                    }
                }
            }
        }
    }
}
