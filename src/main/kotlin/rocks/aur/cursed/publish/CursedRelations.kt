package rocks.aur.cursed.publish

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.jetbrains.annotations.*

@ApiStatus.NonExtendable
@CursedDsl
@SubclassOptInRequired(markerClass = CursedInternalApi::class)
interface CursedRelations {
    @get:Nested
    val projects: NamedDomainObjectContainer<out CursedRelationProject>

    fun project(
        name: String,
        action: Action<in CursedRelationProject>
    ): NamedDomainObjectProvider<out CursedRelationProject> = projects.register(name, action)

    fun project(
        name: String,
        type: CursedRelationProject.RelationType
    ): NamedDomainObjectProvider<out CursedRelationProject> = project(name) {
        this.type.set(type)
    }

    fun embeddedLibrary(name: String) = project(name, CursedRelationProject.RelationType.EmbeddedLibrary)
    fun incompatible(name: String) = project(name, CursedRelationProject.RelationType.Incompatible)
    fun optionalDependency(name: String) = project(name, CursedRelationProject.RelationType.OptionalDependency)
    fun requiredDependency(name: String) = project(name, CursedRelationProject.RelationType.RequiredDependency)
    fun tool(name: String) = project(name, CursedRelationProject.RelationType.Tool)
}