package rocks.aur.cursed.publish.internal

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.cio.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import org.gradle.api.artifacts.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*
import rocks.aur.cursed.publish.internal.model.*

@ApiStatus.Internal
@CursedInternalApi
internal data class CursedPublishAction(
    private val httpClient: HttpClient
) {
    fun publish(file: CursedFile.Version): CursedPublishWorkResult {
        val result = httpClient.async { doPublish(file) }
        return try {
            httpClient.use {
                runBlocking { result.await() }
            }
        } catch (e: Throwable) {
            DefaultCursedPublishWorkResult(e)
        }
    }

    private suspend fun doPublish(file: CursedFile.Version) = coroutineScope {
        val gameVersionTypes = async { get<List<GameVersionType>>("api/game/version-types") }
        val gameVersions = async { get<List<GameVersion>>("api/game/versions") }
        doPublish(gameVersionTypes.await(), gameVersions.await(), file)
    }

    private suspend inline fun <reified T : Any> get(url: String): T {
        val response = httpClient.get(url)
        if (response.status.isSuccess()) {
            return response.body<T>()
        }
        val error = response.body<CurseforgeError>()
        throw PublishException("Failed to publish: ${error.code} - ${error.message}")
    }

    private suspend fun doPublish(
        gameVersionTypes: List<GameVersionType>,
        gameVersions: List<GameVersion>,
        file: CursedFile.Version
    ): DefaultCursedPublishWorkResult {
        val metadata = UploadMetadata(
            changelog = file.changelog.get(),
            changelogType = file.changelogType.get(),
            displayName = file.displayName.orNull,
            parentFileID = null,
            gameVersions = resolve(gameVersionTypes, gameVersions, file.gameVersions.get()),
            releaseType = file.releaseType.get(),
            relations = resolve(file.relations),
        )
        val result = doPublish(file, metadata)
        if (result.exception != null) return result
        file.additionalFiles.forEach {
            val additionalResult = doPublish(result.fileId, it)
            if (additionalResult.exception != null) return additionalResult
            result.addNested(additionalResult)
        }
        return result
    }

    private suspend fun doPublish(
        parentFileID: UInt,
        file: CursedFile.Additional
    ): DefaultCursedPublishWorkResult {
        val metadata = UploadMetadata(
            changelog = file.changelog.get(),
            changelogType = file.changelogType.get(),
            displayName = file.displayName.orNull,
            parentFileID = parentFileID,
            releaseType = file.releaseType.get(),
            relations = resolve(file.relations),
        )
        return doPublish(file, metadata)
    }

    private suspend fun doPublish(
        file: CursedFile,
        metadata: UploadMetadata
    ): DefaultCursedPublishWorkResult {
        val fileToUpload = file.file.get().asFile
        val statement = httpClient.prepareFormWithBinaryData(
            "api/projects/${file.projectId.get()}/upload-file", listOf(
                PartData.FormItem(CursedJson.encodeToString(metadata), {}, buildHeaders {
                    set(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition("form-data").withParameter(
                            ContentDisposition.Parameters.Name,
                            "metadata"
                        ).toString()
                    )
                    set(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.withCharset(Charsets.UTF_8).toString()
                    )
                }),
                PartData.BinaryChannelItem(fileToUpload::readChannel, buildHeaders {
                    set(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition("form-data")
                            .withParameter(ContentDisposition.Parameters.Name, "file")
                            .withParameter(ContentDisposition.Parameters.FileName, fileToUpload.name)
                            .toString()
                    )
                })
            )
        )
        val response = statement.execute()
        return if (response.status.isSuccess()) {
            val result = response.body<UploadFileResult>()
            DefaultCursedPublishWorkResult(result.id, file)
        } else {
            val error = response.body<CurseforgeError>()
            DefaultCursedPublishWorkResult(PublishException("Failed to publish: ${error.code} - ${error.message}"))
        }
    }

    private fun resolve(
        gameVersionTypes: List<GameVersionType>,
        gameVersions: List<GameVersion>,
        candidates: Set<CursedGameVersion>
    ): Set<UInt> = candidates.asSequence().map { it as DefaultCursedGameVersion }.flatMap {
        it.resolve(gameVersions, gameVersionTypes)
    }.map { it.id }.toSet()

    private fun resolve(relations: CursedRelations) = UploadMetadata.Relations(
        projects = relations.projects.map { project ->
            UploadMetadata.Relations.Project(
                slug = project.name,
                type = project.type.get()
            )
        }
    )
}