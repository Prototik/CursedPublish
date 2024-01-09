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

allprojects {
    pluginManager.withPlugin("org.gradle.java-base") {
        java {
            targetCompatibility = JavaVersion.VERSION_1_8
            sourceCompatibility = JavaVersion.VERSION_1_8
        }
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
            allWarningsAsErrors = true
            freeCompilerArgs.addAll(
                "-Xlambdas=indy",
                "-Xjvm-default=all",
                "-Xemit-jvm-type-annotations",
                "-Xnew-inference",
                "-Xassertions=jvm"
            )
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
        implementation(libs.annotations)
    }

    implementation(platform(embeddedKotlin("bom")))
    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(platform(libs.slf4j.bom))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(embeddedKotlin("stdlib-jdk8"))
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.encoding)
    implementation(libs.ktor.serialization.kotlinx.json)

    compileOnlyApi(libs.annotations)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            testType = TestSuiteType.UNIT_TEST
        }

        val integrationTest by registering(JvmTestSuite::class) {
            testType = TestSuiteType.INTEGRATION_TEST

            dependencies {
                implementation(project())
            }

            targets.configureEach {
                testTask {
                    shouldRunAfter(test)
                }
            }
        }

        withType<JvmTestSuite>().configureEach {
            val suiteName = name

            useJUnitJupiter(libs.versions.junit)

            dependencies {
                implementation(platform("org.jetbrains.kotlin:kotlin-bom:$embeddedKotlinVersion"))
                implementation(platform(libs.kotlinx.coroutines.bom))
                implementation(platform(libs.slf4j.bom))
                implementation(platform(libs.junit.bom))
                implementation(projects.testlib)
                implementation.bundle(libs.bundles.kotest)
            }

            targets.configureEach {
                testTask {
                    systemProperty(
                        "kotest.framework.parallelism",
                        Runtime.getRuntime().availableProcessors().coerceAtLeast(2)
                    )
                    systemProperty("kotest.framework.classpath.scanning.config.disable", "true")
                    systemProperty("kotest.framework.config.fqn", "rocks.aur.cursed.publish.test.KotestConfig")

                    jvmArgumentProviders += CommandLineArgumentProvider {
                        listOf(
                            "-Dgradle.build.dir=${project.layout.buildDirectory.get().asFile}",
                            "-Dgradle.test.suite.name=${suiteName}"
                        )
                    }

                    reports {
                        junitXml.required = false
                        html.required = false
                    }

                    System.getenv("CURSED_PUBLISH_TEST_JAVA_VERSION")?.let { javaVersion ->
                        javaLauncher = javaToolchains.launcherFor {
                            languageVersion = JavaLanguageVersion.of(javaVersion)
                        }
                    }

                    outputs.upToDateWhen { System.getenv("CI") == null }
                }
            }
        }
    }
}

gradlePlugin {
    website = "https://github.com/Prototik/CursedPublish"
    vcsUrl = "https://github.com/Prototik/CursedPublish"
    isAutomatedPublishing = true

    testSourceSets(sourceSets["integrationTest"])

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
