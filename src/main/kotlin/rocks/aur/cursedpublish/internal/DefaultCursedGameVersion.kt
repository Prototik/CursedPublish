package rocks.aur.cursedpublish.internal

import io.github.z4kn4fein.semver.*
import io.github.z4kn4fein.semver.constraints.*
import org.gradle.api.model.*
import org.gradle.api.provider.*
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.infer.*
import rocks.aur.cursedpublish.internal.model.*
import javax.inject.*

@ApiStatus.Internal
@CursedInternalApi
internal sealed class DefaultCursedGameVersion : CursedGameVersion, GameVersionInfer {

    internal fun resolveSlug(
        property: Provider<out String>,
        versions: Collection<GameVersion>,
    ): Collection<GameVersion> {
        val slug = property.orNull ?: return emptySet()
        return setOf(versions.single { it.slug == slug })
    }

    internal fun resolveVersion(
        property: Provider<out Constraint>,
        versions: Map<GameVersion, Version?>,
        onlyStable: Boolean = true,
    ): Collection<GameVersion> {
        val constraint = property.orNull ?: return emptySet()
        return versions.filterValues { it != null && (!onlyStable || it.isStable) && constraint.isSatisfiedBy(it) }.keys
    }

    @ApiStatus.Internal
    internal open class Minecraft @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Minecraft {
        @get:Input
        override val version: Property<String> = objects.property()

        @get:Input
        override val onlyStable: Property<Boolean> = objects.property<Boolean>().convention(true)

        override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> =
            resolveVersion(version.map(Constraint::parse), minecraftVersions, onlyStable.get())

        override fun toString() = "CursedGameVersion.Minecraft(version=${version.orNull})"
    }

    @ApiStatus.Internal
    internal open class ModLoader @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.ModLoader {
        @get:Input
        override val type: Property<CursedGameVersion.ModLoader.Type> = objects.property()

        override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> =
            resolveSlug(type.map { it.slug }, modloaders)

        override fun toString() = "CursedGameVersion.ModLoader(type=${type.orNull})"
    }

    @ApiStatus.Internal
    internal open class Java @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Java {
        @get:Input
        override val version: Property<Int> = objects.property()
        override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> =
            resolveVersion(version.map { Constraint.parse("$it") }, javaVersions)

        override fun toString() = "CursedGameVersion.Java(version=${version.orNull})"
    }

    @ApiStatus.Internal
    internal open class Environment @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Environment {
        @get:Input
        override val type: Property<CursedGameVersion.Environment.Type> = objects.property()
        override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> =
            resolveSlug(type.map { it.slug }, environment)

        override fun toString() = "CursedGameVersion.Environment(type=${type.orNull})"
    }
}