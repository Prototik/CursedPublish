@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `jvm-test-suite`
    `kotlin-dsl`
    signing
    alias(libs.plugins.plugin.publish)
    embeddedKotlin("jvm")
    embeddedKotlin("plugin.serialization")
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

    pluginManager.withPlugin("org.gradle.publishing") {
        val publishing: PublishingExtension by extensions

        publishing.repositories {
            val username = "MAVEN_USER".let { System.getenv(it) ?: findProperty(it) }?.toString()
            val password = "MAVEN_PASS".let { System.getenv(it) ?: findProperty(it) }?.toString()
            if (username != null && password != null) {
                maven("https://maven.aur.rocks/releases") {
                    name = "kkIncReleases"
                    mavenContent {
                        releasesOnly()
                    }
                    credentials {
                        this.username = username
                        this.password = password
                    }
                }

                maven("https://maven.aur.rocks/snapshots") {
                    name = "kkIncSnapshots"
                    mavenContent {
                        snapshotsOnly()
                    }
                    credentials {
                        this.username = username
                        this.password = password
                    }
                }

                tasks.register("publishAllPublicationsToKkIncRepository") {
                    dependsOn(
                        if (project.version.toString().endsWith("-SNAPSHOT")) {
                            "publishAllPublicationsToKkIncSnapshotsRepository"
                        } else {
                            "publishAllPublicationsToKkIncReleasesRepository"
                        }
                    )
                }
            }
        }

        publishing.publications.withType<MavenPublication>().configureEach {
            pom {
                description.convention(provider { project.description })

                licenses {
                    license {
                        name = "CC0 1.0 Universal"
                        url = "https://creativecommons.org/publicdomain/zero/1.0/"
                        distribution = "repo"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Prototik/CursedPublish.git"
                    developerConnection = "scm:git:git@github.com:Prototik/CursedPublish.git"
                    url = "https://github.com/Prototik/CursedPublish"
                }

                developers {
                    developer {
                        id = "Prototik"
                        name = "Sergey Shatunov"
                        email = "me@aur.rocks"
                    }
                }
            }
        }
    }
}

subprojects {
    version = rootProject.version
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
                    systemProperty("kotest.framework.config.fqn", "rocks.aur.cursedpublish.testlib.KotestConfig")

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

    testSourceSets(sourceSets["integrationTest"])

    plugins {
        register("cursedPublish") {
            id = "rocks.aur.cursedpublish"
            implementationClass = "rocks.aur.cursedpublish.CursedPublishPlugin"
            description = "Plugin to publish artifacts to the CurseForge portal"
            displayName = "Cursed Publish plugin"
            tags.addAll("curseforge", "publish", "publication", "upload", "release")
        }
    }
}

signing {
    isRequired = System.getenv("RELEASE") == "true"
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}

tasks {
    clean {
        dependsOn(nyxClean)
    }
    nyxMake {
        dependsOn(assemble)
    }
    nyxPublish {
        dependsOn("publishAllPublicationsToKkIncRepository")
//        dependsOn("publishPlugins")
    }
}