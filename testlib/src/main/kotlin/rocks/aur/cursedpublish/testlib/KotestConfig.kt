package rocks.aur.cursedpublish.testlib

import io.kotest.common.*
import io.kotest.core.config.*
import io.kotest.core.extensions.*
import io.kotest.core.names.*
import io.kotest.extensions.htmlreporter.*
import io.kotest.extensions.junitxml.*

@OptIn(ExperimentalKotest::class)
object KotestConfig : AbstractProjectConfig() {
    override val parallelism = Runtime.getRuntime().availableProcessors().coerceAtLeast(2)
    override val concurrentSpecs = ProjectConfiguration.MaxConcurrency
    override val concurrentTests = ProjectConfiguration.MaxConcurrency
    override val duplicateTestNameMode = DuplicateTestNameMode.Error

    private val gradleTestSuiteName = System.getProperty("gradle.test.suite.name", "test")
    override fun extensions(): List<Extension> = listOf(
        JunitXmlReporter(
            outputDir = "test-results/$gradleTestSuiteName"
        ),
        HtmlReporter(
            outputDir = "reports/tests/$gradleTestSuiteName"
        )
    )
}