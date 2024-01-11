package rocks.aur.cursedpublish.internal.infer

import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.model.*

@ApiStatus.Internal
@CursedInternalApi
internal interface Infer : GameVersionInfer {
    override fun Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> = emptyList()

    @ApiStatus.Internal
    @CursedInternalApi
    interface Scope {
        val versions: Collection<GameVersion>
        val versionTypes: Collection<GameVersionType>

        val minecraftVersions: Collection<GameVersion> get() = with(GameVersionInfer.MINECRAFT_RESOLVER) { inferGameVersions() }
        val javaVersions: Collection<GameVersion> get() = with(GameVersionInfer.JAVA_RESOLVER) { inferGameVersions() }
        val modloaders: Collection<GameVersion> get() = with(GameVersionInfer.MODLOADER_RESOLVER) { inferGameVersions() }
        val environment: Collection<GameVersion> get() = with(GameVersionInfer.ENVIRONMENT_RESOLVER) { inferGameVersions() }
    }

    object Empty : Infer
}

@ApiStatus.Internal
@CursedInternalApi
internal fun Infer.decompose(): Collection<Infer> = when (this) {
    Infer.Empty -> listOf()
    is CompositeInfer -> this.infers
    else -> listOf(this)
}

@ApiStatus.Internal
@CursedInternalApi
internal operator fun Infer.plus(infer: Infer): Infer = buildList {
    addAll(this@plus.decompose())
    addAll(infer.decompose())
}.merge()

@ApiStatus.Internal
@CursedInternalApi
internal operator fun Infer.plus(infers: Iterable<Infer>): Infer = buildList {
    addAll(this@plus.decompose())
    infers.forEach { inner ->
        addAll(inner.decompose())
    }
}.merge()

@ApiStatus.Internal
@CursedInternalApi
internal fun Iterable<Infer>.asSingle(): Infer {
    return flatMap { it.decompose() }.merge()
}

@CursedInternalApi
private fun List<Infer>.merge(): Infer {
    return when (this.size) {
        0 -> Infer.Empty
        1 -> this[0]
        else -> CompositeInfer(this)
    }
}

