package rocks.aur.cursed.publish.internal.model

import kotlinx.serialization.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*

@OptIn(ExperimentalSerializationApi::class)
@ApiStatus.Internal
@CursedInternalApi
@Serializable
internal data class UploadMetadata(
    val changelog: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val changelogType: CursedChangelogType = CursedChangelogType.Text,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val displayName: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val parentFileID: UInt? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val gameVersions: Set<UInt>? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val releaseType: CursedReleaseType = CursedReleaseType.Release,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val relations: Relations = Relations(),
) {
    @ApiStatus.Internal
    @CursedInternalApi
    @Serializable
    data class Relations(
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        val projects: List<Project> = emptyList()
    ) {
        @ApiStatus.Internal
        @CursedInternalApi
        @Serializable
        data class Project(
            val slug: String,
            val type: CursedRelationProject.RelationType
        )
    }
}