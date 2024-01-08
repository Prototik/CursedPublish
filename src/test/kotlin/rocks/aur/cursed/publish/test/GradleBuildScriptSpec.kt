package rocks.aur.cursed.publish.test

import io.kotest.core.annotation.*
import io.kotest.core.spec.style.*
import io.kotest.engine.spec.*
import io.kotest.matchers.*
import io.kotest.matchers.nulls.*
import io.kotest.matchers.string.*
import org.gradle.testkit.runner.*
import org.mockserver.integration.*
import org.mockserver.model.*
import org.mockserver.verify.*
import rocks.aur.cursed.publish.test.models.*
import java.io.*

@Ignored
sealed class GradleBuildScriptSpec(
    writeProject: (projectDir: File, apiBaseUrl: String) -> Unit,
) : FunSpec({
    lateinit var projectDir: File

    val gameVersionTypesRequest = HttpRequest.request()
        .withMethod("GET")
        .withPath("/api/game/version-types")
        .withHeader("X-Api-Token", "test-token")

    val gameVersionsRequest = HttpRequest.request()
        .withMethod("GET")
        .withPath("/api/game/versions")
        .withHeader("X-Api-Token", "test-token")

    val uploadFileRequest = HttpRequest.request()
        .withMethod("POST")
        .withPath("/api/projects/123/upload-file")
        .withHeader("X-Api-Token", "test-token")

    lateinit var clientAndServer: ClientAndServer

    beforeSpec {
        clientAndServer = ClientAndServer.startClientAndServer()
    }

    beforeTest {
        projectDir = tempdir()
        clientAndServer.apply {
            `when`(gameVersionTypesRequest).respond(
                HttpResponse.response()
                    .withBody(
                        GameVersionTypesSpec.stream().bufferedReader().readText(),
                        MediaType.APPLICATION_JSON_UTF_8
                    )
            )

            `when`(gameVersionsRequest).respond(
                HttpResponse.response()
                    .withBody(
                        GameVersionsSpec.stream().bufferedReader().readText(),
                        MediaType.APPLICATION_JSON_UTF_8
                    )
            )

            `when`(uploadFileRequest).respond(
                HttpResponse.response()
                    .withBody(
                        "{\"id\": 456}",
                        MediaType.APPLICATION_JSON_UTF_8
                    )
            )
        }
    }

    afterTest {
        clientAndServer.reset()
    }

    afterSpec {
        clientAndServer.close()
    }

    test("publication successful") {
        writeProject(projectDir, "http://localhost:${clientAndServer.port}")

        val runner = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(":curseforgeUploadJarFile", "--warning-mode", "all")

        System.getenv("CURSED_PUBLISH_TEST_GRADLE_VERSION")?.let { gradleVersion ->
            runner.withGradleVersion(gradleVersion)
        }

        val result = runner.build()

        result.output shouldNotContain "Deprecated Gradle features were used in this build"

        val taskResult = result.task(":curseforgeUploadJarFile")
        taskResult.shouldNotBeNull()
        taskResult.outcome shouldBe TaskOutcome.SUCCESS

        clientAndServer.verify(gameVersionTypesRequest, VerificationTimes.atLeast(1))
        clientAndServer.verify(gameVersionsRequest, VerificationTimes.atLeast(1))
        clientAndServer.verify(uploadFileRequest, VerificationTimes.exactly(2))
    }
})

object KotlinGradleBuildScriptSpec : GradleBuildScriptSpec({ projectDir, apiBaseUrl ->
    projectDir.resolve("build.gradle.kts").writeText(
        """
        import rocks.aur.cursed.publish.*
            
        plugins {
            id("rocks.aur.cursed.publish")
            `java-library`
        }
                       
        java {
            withSourcesJar()
        }
                        
        cursedPublish {
            projectId("123")
            apiBaseUrl("$apiBaseUrl")
            apiToken("test-token")
            
            file(tasks.jar) {
                displayName("MyCoolMod (Fabric)")
                changelog(CursedChangelogType.Text, "Some changelog")
            
                minecraft("1.20.4")
                fabric()
                java(17)
                client()
                
                releaseType(CursedReleaseType.Alpha)
            
                relations {
                    requiredDependency("tcl")
                }
                                
                additionalFile(tasks.named<Jar>("sourcesJar")) {
                    beta()
                }
            }
        }                        
        """.trimIndent()
    )

    projectDir.resolve("settings.gradle.kts").writeText(
        """
        rootProject.name = "test"
        """.trimIndent()
    )
})

object GroovyGradleBuildScriptSpec : GradleBuildScriptSpec({ projectDir, apiBaseUrl ->
    projectDir.resolve("build.gradle").writeText(
        """
        import rocks.aur.cursed.publish.*
            
        plugins {
            id 'rocks.aur.cursed.publish'
            id 'java-library'
        }
                        
        java {
            withSourcesJar()
        }
                        
        cursedPublish {
            projectId '123'
            apiBaseUrl '$apiBaseUrl'
            apiToken 'test-token'
            
            file(tasks.jar) {
                displayName 'MyCoolMod (Fabric)'
                changelog CursedChangelogType.Text, 'Some changelog'
            
                minecraft '1.20.4'
                fabric()
                java 17
                client()
        
                releaseType CursedReleaseType.Alpha
            
                relations {
                    requiredDependency 'tcl'
                }
                                
                additionalFile(tasks.sourcesJar) {
                    beta()
                }
            }
        }                                        
        """.trimIndent()
    )

    projectDir.resolve("settings.gradle").writeText(
        """
        rootProject.name = 'test'
        """.trimIndent()
    )
})