package rocks.aur.cursed.publish.internal

import org.gradle.api.model.*
import org.gradle.api.provider.*
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*
import rocks.aur.cursed.publish.internal.model.*
import javax.inject.*

@ApiStatus.Internal
@CursedInternalApi
internal sealed class DefaultCursedGameVersion : CursedGameVersion {
    abstract fun resolve(
        versions: Collection<GameVersion>,
        versionTypes: Collection<GameVersionType>
    ): Sequence<GameVersion>

    internal suspend inline fun SequenceScope<GameVersion>.resolveName(
        property: Provider<out String>,
        versionsToLookup: () -> Collection<GameVersion>,
    ) {
        val name = property.orNull ?: return
        val versions = versionsToLookup.invoke()
        yield(versions.single { it.name == name })
    }

    internal suspend inline fun SequenceScope<GameVersion>.resolveSlug(
        property: Provider<out String>,
        versionsToLookup: () -> Collection<GameVersion>,
    ) {
        val slug = property.orNull ?: return
        val versions = versionsToLookup.invoke()
        yield(versions.single { it.slug == slug })
    }

    @ApiStatus.Internal
    internal open class Minecraft @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Minecraft {
        @get:Input
        override val version: Property<String> = objects.property()
        override fun resolve(
            versions: Collection<GameVersion>,
            versionTypes: Collection<GameVersionType>
        ): Sequence<GameVersion> = sequence {
            resolveName(version) { MINECRAFT_RESOLVER.resolve(versions, versionTypes) }
        }

        companion object {
            private val MINECRAFT_RESOLVER = GameVersionResolver.ByTypeSlugPrefix("minecraft-")
        }
    }

    @ApiStatus.Internal
    internal open class ModLoader @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.ModLoader {
        @get:Input
        override val type: Property<CursedGameVersion.ModLoader.Type> = objects.property()

        override fun resolve(
            versions: Collection<GameVersion>,
            versionTypes: Collection<GameVersionType>
        ): Sequence<GameVersion> = sequence {
            resolveSlug(type.map { it.slug }) { MODLOADER_RESOLVER.resolve(versions, versionTypes) }
        }

        companion object {
            private val MODLOADER_RESOLVER = GameVersionResolver.ByTypeSlug("modloader")
        }
    }

    @ApiStatus.Internal
    internal open class Java @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Java {
        @get:Input
        override val version: Property<Int> = objects.property()
        override fun resolve(
            versions: Collection<GameVersion>,
            versionTypes: Collection<GameVersionType>
        ): Sequence<GameVersion> = sequence {
            resolveSlug(version.map { "java-$it" }) { JAVA_RESOLVER.resolve(versions, versionTypes) }
        }

        companion object {
            private val JAVA_RESOLVER = GameVersionResolver.ByTypeSlug("java")
        }
    }

    @ApiStatus.Internal
    internal open class Environment @Inject constructor(
        objects: ObjectFactory
    ) : DefaultCursedGameVersion(), CursedGameVersion.Environment {
        @get:Input
        override val type: Property<CursedGameVersion.Environment.Type> = objects.property()
        override fun resolve(
            versions: Collection<GameVersion>,
            versionTypes: Collection<GameVersionType>
        ): Sequence<GameVersion> = sequence {
            resolveSlug(type.map { it.slug }) { ENVIRONMENT_RESOLVER.resolve(versions, versionTypes) }
        }

        companion object {
            private val ENVIRONMENT_RESOLVER = GameVersionResolver.ByTypeSlug("environment")
        }
    }

}