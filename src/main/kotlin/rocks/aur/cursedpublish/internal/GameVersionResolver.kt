package rocks.aur.cursedpublish.internal

import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.model.*

@ApiStatus.Internal
@CursedInternalApi
internal fun interface GameVersionResolver {
    fun resolve(
        versions: Collection<GameVersion>,
        versionTypes: Collection<GameVersionType>
    ): Collection<GameVersion>

    @ApiStatus.Internal
    abstract class ByType : GameVersionResolver {
        @OptIn(ExperimentalUnsignedTypes::class)
        override fun resolve(
            versions: Collection<GameVersion>,
            versionTypes: Collection<GameVersionType>
        ): Collection<GameVersion> {
            val types = versionTypes.filter(this::isGameVersionTypeApplicable).map { it.id }.toUIntArray()
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
}