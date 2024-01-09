package rocks.aur.cursedpublish.internal

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import org.gradle.api.*
import org.gradle.api.model.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.internal.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import java.net.*
import javax.inject.*

@ApiStatus.Internal
@CursedInternalApi
internal open class DefaultCursedPublishSpec @Inject constructor(
    private val objects: ObjectFactory
) : CursedPublishSpec {
    @get:Input
    override val apiBaseUrl: Property<URI> =
        objects.property<URI>().convention(URI.create(CursedPublishSpec.API_BASE_URL))

    @get:Input
    override val apiToken: Property<String> = objects.property()

    @get:Input
    override val projectId: Property<UInt> = objects.property()

    @get:Nested
    final override val files: NamedDomainObjectContainer<DefaultCursedFile.Version> =
        objects.domainObjectContainer(DefaultCursedFile.Version::class) { name ->
            val file = objects.newInstance<DefaultCursedFile.Version>(name)
            file.projectId.convention(projectId)
            file
        }

    private var httpClientInitializer: CursedPublishSpec.HttpClientInitializer =
        CursedPublishSpec.HttpClientInitializer.Default

    override fun httpClientInitializer(initializer: CursedPublishSpec.HttpClientInitializer) {
        this.httpClientInitializer = initializer
    }

    private var httpClientConfiguration: MutableList<Action<in HttpClientConfig<*>>> = mutableListOf(Action {
        install(HttpCookies)
        install(ContentNegotiation) {
            json(CursedJson)
        }
        install(ContentEncoding) {
            gzip(1.0f)
            deflate(0.9f)
            identity(0.8f)
        }
        install(DefaultRequest) {
            url(apiBaseUrl.get().toString())
            headers["X-Api-Token"] = apiToken.get()
        }
    })

    override fun httpClient(action: Action<in HttpClientConfig<*>>) {
        httpClientConfiguration += action
    }

    internal fun createHttpClient(): HttpClient {
        return httpClientInitializer.create(Actions.composite(httpClientConfiguration))
    }

    override fun toString() = "CursedPublishSpec"
}