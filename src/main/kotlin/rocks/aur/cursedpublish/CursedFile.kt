package rocks.aur.cursedpublish

import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*
import org.gradle.internal.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.annotations.*
import java.io.*

@Suppress("unused")
@ApiStatus.NonExtendable
@CursedDsl
@SubclassOptInRequired(markerClass = CursedInternalApi::class)
interface CursedFile : Named, Buildable {
    @Internal
    override fun getName(): String

    @get:Input
    val changelog: Property<String>

    fun changelog(changelog: String) {
        this.changelog.set(changelog)
    }

    @get:Input
    val changelogType: Property<CursedChangelogType>

    fun changelogType(changelogType: CursedChangelogType) {
        this.changelogType.set(changelogType)
    }

    fun changelog(changelogType: CursedChangelogType, changelog: String) {
        changelogType(changelogType)
        changelog(changelog)
    }

    fun changelogText(changelog: String) = changelog(CursedChangelogType.Text, changelog)
    fun changelogHtml(changelog: String) = changelog(CursedChangelogType.Html, changelog)
    fun changelogMarkdown(changelog: String) = changelog(CursedChangelogType.Markdown, changelog)

    @get:Input
    @get:Optional
    val displayName: Property<String>

    fun displayName(displayName: String) {
        this.displayName.set(displayName)
    }

    @get:Input
    val releaseType: Property<CursedReleaseType>

    fun releaseType(releaseType: CursedReleaseType) {
        this.releaseType.set(releaseType)
    }

    fun alpha() = releaseType(CursedReleaseType.Alpha)
    fun beta() = releaseType(CursedReleaseType.Beta)
    fun release() = releaseType(CursedReleaseType.Release)

    @get:InputFile
    val file: RegularFileProperty
    fun from(file: RegularFile) {
        this.file.set(file)
    }

    fun from(file: File) {
        this.file.set(file)
    }

    fun from(task: AbstractArchiveTask) {
        this.file.set(task.archiveFile)
        dependsOn(task)
    }

    fun from(task: Provider<out AbstractArchiveTask>) {
        this.file.set(task.flatMap { it.archiveFile })
        dependsOn(task)
    }

    @get:Input
    val projectId: Provider<UInt>

    @get:Nested
    val relations: CursedRelations

    fun relations(action: Action<in CursedRelations>) {
        action(relations)
    }

    @Input
    override fun getBuildDependencies(): TaskDependency

    fun dependsOn(vararg paths: Any)

    interface Version : CursedFile {
        @get:Input
        override val projectId: Property<UInt>

        fun projectId(projectId: UInt) {
            this.projectId.set(projectId)
        }

        fun projectId(projectId: String) {
            projectId(
                projectId.toUIntOrNull()
                    ?: throw IllegalArgumentException("Unable to coerce value \"${projectId}\" to integer")
            )
        }

        @get:Nested
        val additionalFiles: NamedDomainObjectContainer<out Additional>

        fun additionalFile(name: String, action: Action<in Additional>): NamedDomainObjectProvider<out Additional> =
            additionalFiles.register(name, action)

        fun additionalFile(name: String): NamedDomainObjectProvider<out Additional> =
            additionalFile(name, Actions.doNothing())

        fun additionalFile(
            task: AbstractArchiveTask,
            action: Action<in Additional>
        ): NamedDomainObjectProvider<out Additional> = additionalFile(task.name) {
            from(task)
            action(this)
        }

        fun additionalFile(task: AbstractArchiveTask): NamedDomainObjectProvider<out Additional> =
            additionalFile(task, Actions.doNothing())

        fun additionalFile(
            task: NamedDomainObjectProvider<out AbstractArchiveTask>,
            action: Action<in Additional>
        ): NamedDomainObjectProvider<out Additional> = additionalFile(task.name) {
            from(task)
            action(this)
        }

        fun additionalFile(task: NamedDomainObjectProvider<out AbstractArchiveTask>): NamedDomainObjectProvider<out Additional> =
            additionalFile(task, Actions.doNothing())

        @get:Input
        val gameVersions: SetProperty<out CursedGameVersion>

        fun minecraft(action: Action<in CursedGameVersion.Minecraft>): CursedGameVersion.Minecraft

        fun minecraft(version: String) = minecraft {
            this.version.set(version)
        }

        fun modLoader(action: Action<in CursedGameVersion.ModLoader>): CursedGameVersion.ModLoader

        fun modLoader(type: CursedGameVersion.ModLoader.Type) = modLoader {
            this.type.set(type)
        }

        fun forge() = modLoader(CursedGameVersion.ModLoader.Type.Forge)
        fun neoForge() = modLoader(CursedGameVersion.ModLoader.Type.NeoForge)
        fun fabric() = modLoader(CursedGameVersion.ModLoader.Type.Fabric)
        fun quilt() = modLoader(CursedGameVersion.ModLoader.Type.Quilt)
        fun rift() = modLoader(CursedGameVersion.ModLoader.Type.Rift)
        fun risugamisModLoader() = modLoader(CursedGameVersion.ModLoader.Type.RisugamisModLoader)

        fun java(action: Action<in CursedGameVersion.Java>): CursedGameVersion.Java
        fun java(version: Int) = java {
            this.version.set(version)
        }

        fun environment(action: Action<in CursedGameVersion.Environment>): CursedGameVersion.Environment
        fun environment(type: CursedGameVersion.Environment.Type) = environment {
            this.type.set(type)
        }

        fun client() = environment(CursedGameVersion.Environment.Type.Client)
        fun server() = environment(CursedGameVersion.Environment.Type.Server)
    }

    interface Additional : CursedFile
}