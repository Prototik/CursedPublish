package rocks.aur.cursedpublish.internal

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
    internal fun MutableCollection<GameVersion>.resolveName(
        property: Provider<out String>,
        versionsToLookup: () -> Collection<GameVersion>,
    ) {
        val name = property.orNull ?: return
        val versions = versionsToLookup.invoke()
        this += versions.single { it.name == name }
    }

    internal fun MutableCollection<GameVersion>.resolveSlug(
        property: Provider<out String>,
        versionsToLookup: () -> Collection<GameVersion>,
    ) {
        val slug = property.orNull ?: return
        val versions = versionsToLookup.invoke()
        this += versions.single { it.slug == slug }
    }

    @ApiStatus.Internal
    internal open class Minecraft @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Minecraft {
        @get:Input
        override val version: Property<String> = objects.property()

        override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> = buildList {
            resolveName(version) { minecraftVersions }
        }

        override fun toString() = "CursedGameVersion.Minecraft(version=${version.orNull})"
    }

    @ApiStatus.Internal
    internal open class ModLoader @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.ModLoader {
        @get:Input
        override val type: Property<CursedGameVersion.ModLoader.Type> = objects.property()

        override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> = buildList {
            resolveSlug(type.map { it.slug }) { modloaders }
        }

        override fun toString() = "CursedGameVersion.ModLoader(type=${type.orNull})"
    }

    @ApiStatus.Internal
    internal open class Java @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Java {
        @get:Input
        override val version: Property<Int> = objects.property()
        override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> = buildList {
            resolveSlug(version.map { "java-$it" }) { javaVersions }
        }

        override fun toString() = "CursedGameVersion.Java(version=${version.orNull})"
    }

    @ApiStatus.Internal
    internal open class Environment @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Environment {
        @get:Input
        override val type: Property<CursedGameVersion.Environment.Type> = objects.property()
        override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> = buildList {
            resolveSlug(type.map { it.slug }) { environment }
        }

        override fun toString() = "CursedGameVersion.Environment(type=${type.orNull})"
    }
}