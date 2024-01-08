package rocks.aur.cursed.publish

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.apache5.*
import org.gradle.api.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*
import org.gradle.internal.*
import org.jetbrains.annotations.*
import java.net.*

@ApiStatus.NonExtendable
@CursedDsl
@SubclassOptInRequired(markerClass = CursedInternalApi::class)
interface CursedPublishSpec {
    @get:Input
    val apiBaseUrl: Property<URI>

    fun apiBaseUrl(apiBaseUrl: URI) {
        this.apiBaseUrl.set(apiBaseUrl)
    }

    fun apiBaseUrl(apiBaseUrl: URL) {
        apiBaseUrl(apiBaseUrl.toURI())
    }

    fun apiBaseUrl(apiBaseUrl: String) {
        apiBaseUrl(URI.create(apiBaseUrl))
    }

    @get:Input
    val apiToken: Property<String>

    fun apiToken(apiToken: String) {
        this.apiToken.set(apiToken)
    }

    @get:Input
    val projectId: Property<UInt>

    fun projectId(projectId: UInt) {
        this.projectId.set(projectId)
    }

    fun projectId(projectId: String) {
        projectId(
            projectId.toUIntOrNull()
                ?: throw IllegalArgumentException("Unable to coerce value \"${projectId}\" to integer")
        )
    }

    @get:Nested
    val files: NamedDomainObjectContainer<out CursedFile.Version>

    fun files(action: Action<in NamedDomainObjectContainer<out CursedFile.Version>>) {
        action.execute(files)
    }

    fun file(name: String, action: Action<in CursedFile.Version>): NamedDomainObjectProvider<out CursedFile.Version> {
        return files.register(name, action)
    }

    fun file(name: String): NamedDomainObjectProvider<out CursedFile.Version> = file(name, Actions.doNothing())

    fun file(
        task: AbstractArchiveTask,
        action: Action<in CursedFile.Version>
    ): NamedDomainObjectProvider<out CursedFile.Version> {
        return file(task.name) {
            from(task)
            action.execute(this)
        }
    }

    fun file(task: AbstractArchiveTask): NamedDomainObjectProvider<out CursedFile.Version> =
        file(task, Actions.doNothing())

    fun file(
        task: NamedDomainObjectProvider<out AbstractArchiveTask>,
        action: Action<in CursedFile.Version>
    ): NamedDomainObjectProvider<out CursedFile.Version> {
        return file(task.name) {
            from(task)
            action.execute(this)
        }
    }

    fun file(task: NamedDomainObjectProvider<out AbstractArchiveTask>): NamedDomainObjectProvider<out CursedFile.Version> =
        file(task, Actions.doNothing())

    fun httpClientInitializer(initializer: HttpClientInitializer)
    fun httpClientInitializer(engine: HttpClientEngine) =
        httpClientInitializer(HttpClientInitializer.WithEngine(engine))

    fun <T : HttpClientEngineConfig> httpClientInitializer(
        factory: HttpClientEngineFactory<T>,
        factoryConfig: Action<in HttpClientConfig<T>> = Actions.doNothing(),
    ) = httpClientInitializer(HttpClientInitializer.WithEngineFactory(factory, factoryConfig))

    fun httpClient(action: Action<in HttpClientConfig<*>>)

    companion object {
        const val API_BASE_URL = "https://minecraft.curseforge.com"
    }

    sealed interface HttpClientInitializer {
        fun create(config: Action<in HttpClientConfig<*>>): HttpClient

        object Default : HttpClientInitializer {
            override fun create(config: Action<in HttpClientConfig<*>>): HttpClient {
                return HttpClient(Apache5) {
                    config.execute(this)
                }
            }

            override fun toString() = "Default"
        }

        data class WithEngine(private val engine: HttpClientEngine) : HttpClientInitializer {
            override fun create(config: Action<in HttpClientConfig<*>>): HttpClient {
                return HttpClient(engine) {
                    config.execute(this)
                }
            }
        }

        data class WithEngineFactory<T : HttpClientEngineConfig>(
            private val factory: HttpClientEngineFactory<T>,
            private val factoryConfig: Action<in HttpClientConfig<T>> = Actions.doNothing(),
        ) : HttpClientInitializer {
            override fun create(config: Action<in HttpClientConfig<*>>): HttpClient {
                return HttpClient(factory) {
                    factoryConfig.execute(this)
                    config.execute(this)
                }
            }
        }
    }
}