package rocks.aur.cursed.publish.internal

import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.internal.tasks.*
import org.gradle.api.model.*
import org.gradle.api.provider.*
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*
import javax.inject.*

@ApiStatus.Internal
@CursedInternalApi
internal sealed class DefaultCursedFile @Inject constructor(
    private val name: String,
    objects: ObjectFactory,
    taskDependencyFactory: TaskDependencyFactory,
) : CursedFile {
    @Internal
    override fun getName(): String = name

    @get:Input
    override val changelog: Property<String> = objects.property<String>().convention("")

    @get:Input
    override val changelogType: Property<CursedChangelogType> =
        objects.property<CursedChangelogType>().convention(CursedChangelogType.Text)

    @get:Input
    @get:Optional
    override val displayName: Property<String> = objects.property()

    @get:Input
    override val releaseType: Property<CursedReleaseType> =
        objects.property<CursedReleaseType>().convention(CursedReleaseType.Alpha)

    @get:InputFile
    override val file: RegularFileProperty = objects.fileProperty()

    @get:Nested
    override val relations: DefaultCursedRelations = objects.newInstance()

    private val dependencies: DefaultTaskDependency = taskDependencyFactory.configurableDependency()

    @Input
    override fun getBuildDependencies(): TaskDependency = dependencies

    override fun dependsOn(vararg paths: Any) {
        dependencies.add(*paths)
    }

    internal open class Version @Inject constructor(
        name: String,
        private val objects: ObjectFactory,
        taskDependencyFactory: TaskDependencyFactory,
    ) : DefaultCursedFile(name, objects, taskDependencyFactory), CursedFile.Version {
        @get:Input
        override val projectId: Property<UInt> = objects.property()

        @get:Nested
        override val additionalFiles: NamedDomainObjectContainer<Additional> =
            objects.domainObjectContainer(Additional::class) { name ->
                objects.newInstance<Additional>(name, this)
            }

        @get:Input
        override val gameVersions: SetProperty<DefaultCursedGameVersion> = objects.setProperty()

        private inline fun <reified T : DefaultCursedGameVersion> gameVersion(
            action: Action<in T>,
            vararg parameters: Any
        ): T {
            val gameVersion = objects.newInstance<T>(*parameters)
            action.execute(gameVersion)
            gameVersions.add(gameVersion)
            return gameVersion
        }

        override fun minecraft(
            action: Action<in CursedGameVersion.Minecraft>
        ) = gameVersion<DefaultCursedGameVersion.Minecraft>(action)

        override fun modLoader(
            action: Action<in CursedGameVersion.ModLoader>
        ) = gameVersion<DefaultCursedGameVersion.ModLoader>(action)

        override fun java(
            action: Action<in CursedGameVersion.Java>
        ) = gameVersion<DefaultCursedGameVersion.Java>(action)

        override fun environment(
            action: Action<in CursedGameVersion.Environment>
        ) = gameVersion<DefaultCursedGameVersion.Environment>(action)

        override fun toString() = "CursedFile.Version(name=$name)"
    }

    internal open class Additional @Inject constructor(
        name: String,
        private val parent: Version,
        objects: ObjectFactory,
        taskDependencyFactory: TaskDependencyFactory,
    ) : DefaultCursedFile(name, objects, taskDependencyFactory), CursedFile.Additional {
        @get:Input
        override val projectId: Provider<UInt>
            get() = parent.projectId

        override fun toString() = "CursedFile.Additional(name=$name)"
    }
}