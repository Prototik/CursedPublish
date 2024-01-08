@file:OptIn(ExperimentalTypeInference::class)

package rocks.aur.cursed.publish.test.internal

import io.kotest.core.names.*
import io.kotest.core.spec.style.scopes.*
import io.kotest.core.test.*
import io.kotest.datatest.*
import kotlin.experimental.*

suspend fun <T> ContainerScope.withDataTest(
    first: T,
    second: T,
    vararg rest: T,
    test: suspend TestScope.(T) -> Unit
) = withDataTest(listOf(first, second) + rest, test)

fun <T> RootScope.withDataTest(
    first: T,
    second: T,
    vararg rest: T,
    test: suspend TestScope.(T) -> Unit
) = withDataTest(listOf(first, second) + rest, test)

suspend fun <T> ContainerScope.withDataTest(
    ts: Sequence<T>,
    test: suspend TestScope.(T) -> Unit
) = withDataTest(ts.toList(), test)

fun <T> RootScope.withDataTest(
    ts: Sequence<T>,
    test: suspend TestScope.(T) -> Unit
) = withDataTest(ts.toList(), test)

suspend fun <T> ContainerScope.withDataTest(
    ts: Iterable<T>,
    test: suspend TestScope.(T) -> Unit
) = withDataTest({ getStableIdentifier(it) }, ts, test)

fun <T> RootScope.withDataTest(
    ts: Iterable<T>,
    test: suspend TestScope.(T) -> Unit
) = withDataTest({ getStableIdentifier(it) }, ts, test)

suspend fun <T> ContainerScope.withDataTest(
    nameFn: (T) -> String,
    ts: Sequence<T>,
    test: suspend TestScope.(T) -> Unit
) = withDataTest(nameFn, ts.toList(), test)

fun <T> RootScope.withDataTest(
    nameFn: (T) -> String,
    ts: Sequence<T>,
    test: suspend TestScope.(T) -> Unit
) = withDataTest(nameFn, ts.toList(), test)

suspend fun <T> ContainerScope.withDataTest(
    nameFn: (T) -> String,
    first: T,
    second: T,
    vararg rest: T,
    test: suspend TestScope.(T) -> Unit
) = withDataTest(nameFn, listOf(first, second) + rest, test)

fun <T> RootScope.withDataTest(
    nameFn: (T) -> String,
    first: T,
    second: T,
    vararg rest: T,
    test: suspend TestScope.(T) -> Unit
) = withDataTest(nameFn, listOf(first, second) + rest, test)

suspend fun <T> ContainerScope.withDataTest(
    nameFn: (T) -> String,
    @BuilderInference ts: Iterable<T>,
    @BuilderInference test: suspend TestScope.(T) -> Unit
) = withDataTestImpl(ts.asSequence().map { nameFn(it) to it }, test)

fun <T> RootScope.withDataTest(
    nameFn: (T) -> String,
    @BuilderInference ts: Iterable<T>,
    @BuilderInference test: suspend TestScope.(T) -> Unit
) = withDataTestImpl(ts.asSequence().map { nameFn(it) to it }, test)

@JvmName("withDataTestMap")
suspend fun <T> ContainerScope.withDataTest(data: Map<String, T>, test: suspend TestScope.(T) -> Unit) =
    withDataTestImpl(data.asSequence().map { (name, t) -> name to t }, test)

@JvmName("withDataTestMap")
fun <T> RootScope.withDataTest(data: Map<String, T>, test: suspend TestScope.(T) -> Unit) =
    withDataTestImpl(data.asSequence().map { (name, t) -> name to t }, test)

private suspend inline fun <T> ContainerScope.withDataTestImpl(
    data: Sequence<Pair<String, T>>,
    crossinline test: suspend TestScope.(T) -> Unit
) = data.forEach { (name, t) ->
    registerTest(TestName(name), false, null) { this.test(t) }
}

private inline fun <T> RootScope.withDataTestImpl(
    data: Sequence<Pair<String, T>>,
    crossinline test: suspend TestScope.(T) -> Unit
) = data.forEach { (name, t) ->
    addTest(TestName(name), false, null) { this.test(t) }
}
