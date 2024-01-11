package rocks.aur.cursedpublish.internal.infer

import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.model.*

@ApiStatus.Internal
@CursedInternalApi
internal fun interface GameVersionInfer {
    fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion>

    @ApiStatus.Internal
    abstract class ByType : GameVersionInfer {
        override fun Infer.Scope.inferGameVersions(file: CursedFile) = inferGameVersions()

        @OptIn(ExperimentalUnsignedTypes::class)
        internal fun Infer.Scope.inferGameVersions(): Collection<GameVersion> {
            val types = versionTypes.filter(::isGameVersionTypeApplicable).map { it.id }.toUIntArray()
            return versions.filter { it.typeId in types }
        }

        abstract fun isGameVersionTypeApplicable(gameVersionType: GameVersionType): Boolean
    }

    @ApiStatus.Internal
    data class ByTypeSlug(val slug: String) : ByType() {
        override fun isGameVersionTypeApplicable(gameVersionType: GameVersionType): Boolean {
            return gameVersionType.slug == slug
        }
    }

    @ApiStatus.Internal
    data class ByTypeSlugPrefix(val slugPrefix: String) : ByType() {
        override fun isGameVersionTypeApplicable(gameVersionType: GameVersionType): Boolean {
            return gameVersionType.slug.startsWith(slugPrefix)
        }
    }

    companion object {
        internal val MINECRAFT_RESOLVER = ByTypeSlugPrefix("minecraft-")
        internal val MODLOADER_RESOLVER = ByTypeSlug("modloader")
        internal val JAVA_RESOLVER = ByTypeSlug("java")
        internal val ENVIRONMENT_RESOLVER = ByTypeSlug("environment")
    }
}