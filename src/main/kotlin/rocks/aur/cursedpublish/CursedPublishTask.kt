package rocks.aur.cursedpublish

import io.ktor.client.*
import org.gradle.api.*
import org.gradle.api.internal.tasks.*
import org.gradle.api.model.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.gradle.workers.internal.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.internal.*
import rocks.aur.cursedpublish.internal.infer.*
import javax.inject.*

@OptIn(CursedInternalApi::class)
open class CursedPublishTask @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
) : DefaultTask() {
    @get:Nested
    val file: Property<CursedFile.Version> = objects.property<CursedFile.Version>().convention(providers.provider {
        objects.newInstance<DefaultCursedFile.Version>("detached")
    })

    fun file(action: Action<in CursedFile.Version>) {
        file.finalizeValue()
        action.execute(file.get())
    }

    @CursedInternalApi
    @ApiStatus.Internal
    @get:Internal
    internal val infer: Property<Infer> = objects.property<Infer>().convention(Infer.Empty)

    private val httpClient: () -> HttpClient

    init {
        val extension = project.extensions.getByName<DefaultCursedPublishExtension>(CursedPublishExtension.NAME)
        httpClient = extension::createHttpClient
    }

    @TaskAction
    protected fun upload() {
        val result = doUpload(file.get())
        didWork = result.didWork
        if (result is DefaultWorkResult) {
            result.exception?.let { cause ->
                state.setOutcome(TaskExecutionOutcome.EXECUTED)
                state.addFailure(TaskExecutionException(this, cause))
            }
        }
    }

    private fun doUpload(file: CursedFile.Version): CursedPublishWorkResult {
        return CursedPublishAction(httpClient(), logger, infer.get()).publish(file)
    }
}