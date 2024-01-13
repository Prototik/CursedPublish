package rocks.aur.cursedpublish.testlib

import io.kotest.core.*
import io.kotest.core.listeners.*
import io.kotest.core.test.*
import java.nio.file.*
import kotlin.io.path.*
import kotlin.properties.*
import kotlin.reflect.*

fun TestConfiguration.tempfilePerTest(prefix: String? = null, suffix: String? = null): ReadOnlyProperty<Any?, Path> =
    object : TempPathProvider() {
        override fun createPath(): Path {
            return createTempFile(prefix ?: this@tempfilePerTest.javaClass.name, suffix)
        }
    }.also(::register)

@OptIn(ExperimentalPathApi::class)
private abstract class TempPathProvider : ReadOnlyProperty<Any?, Path>, TestListener {
    private val stack = ArrayDeque<Path>()

    abstract fun createPath(): Path

    override suspend fun beforeAny(testCase: TestCase) {
        stack.add(createPath())
    }

    override suspend fun afterAny(testCase: TestCase, result: TestResult) {
        val file = stack.removeLast()
        file.deleteRecursively()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Path {
        return stack.last()
    }
}