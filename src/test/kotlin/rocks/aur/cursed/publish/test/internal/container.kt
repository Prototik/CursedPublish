@file:OptIn(ExperimentalTypeInference::class)

package rocks.aur.cursed.publish.test.internal

import io.kotest.core.names.*
import io.kotest.core.spec.style.scopes.*
import io.kotest.datatest.*
import kotlin.experimental.*

suspend fun <T> ContainerScope.withDataContainer(
    first: T, second: T, vararg rest: T, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer(listOf(first, second) + rest, test)

fun <T> RootScope.withDataContainer(
    first: T, second: T, vararg rest: T, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer(listOf(first, second) + rest, test)

suspend fun <T> ContainerScope.withDataContainer(
    ts: Sequence<T>, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer(ts.toList(), test)

fun <T> RootScope.withDataContainer(
    ts: Sequence<T>, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer(ts.toList(), test)

suspend fun <T> ContainerScope.withDataContainer(
    ts: Iterable<T>, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer({ getStableIdentifier(it) }, ts, test)

fun <T> RootScope.withDataContainer(
    ts: Iterable<T>, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer({ getStableIdentifier(it) }, ts, test)

suspend fun <T> ContainerScope.withDataContainer(
    nameFn: (T) -> String, ts: Sequence<T>, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer(nameFn, ts.toList(), test)

fun <T> RootScope.withDataContainer(
    nameFn: (T) -> String, ts: Sequence<T>, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer(nameFn, ts.toList(), test)

suspend fun <T> ContainerScope.withDataContainer(
    nameFn: (T) -> String, first: T, second: T, vararg rest: T, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer(nameFn, listOf(first, second) + rest, test)

fun <T> RootScope.withDataContainer(
    nameFn: (T) -> String, first: T, second: T, vararg rest: T, test: suspend ContainerScope.(T) -> Unit
) = withDataContainer(nameFn, listOf(first, second) + rest, test)

suspend fun <T> ContainerScope.withDataContainer(
    nameFn: (T) -> String, @BuilderInference ts: Iterable<T>, @BuilderInference test: suspend ContainerScope.(T) -> Unit
) = withDataContainerImpl(ts.asSequence().map { nameFn(it) to it }, test)

@JvmName("withDataContainerMap")
suspend fun <T> ContainerScope.withDataContainer(data: Map<String, T>, test: suspend ContainerScope.(T) -> Unit) =
    withDataContainerImpl(data.asSequence().map { (name, t) -> name to t }, test)


private suspend inline fun <T> ContainerScope.withDataContainerImpl(
    data: Sequence<Pair<String, T>>, crossinline test: suspend ContainerScope.(T) -> Unit
) = data.forEach { (name, t) ->
    registerContainer(TestName(name), false, null) { AbstractContainerScope(this).test(t) }
}

fun <T> RootScope.withDataContainer(
    nameFn: (T) -> String, @BuilderInference ts: Iterable<T>, @BuilderInference test: suspend ContainerScope.(T) -> Unit
) = withDataContainerImpl(ts.asSequence().map { nameFn(it) to it }, test)

@JvmName("withDataContainerMap")
fun <T> RootScope.withDataContainer(
    data: Map<String, T>, test: suspend ContainerScope.(T) -> Unit
) = withDataContainerImpl(data.asSequence().map { (name, t) -> name to t }, test)

private inline fun <T> RootScope.withDataContainerImpl(
    data: Sequence<Pair<String, T>>, crossinline test: suspend ContainerScope.(T) -> Unit
) = data.forEach { (name, t) ->
    addContainer(TestName(name), false, null) { this.test(t) }
}

