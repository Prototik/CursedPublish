package rocks.aur.cursedpublish.internal

import org.gradle.api.model.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import javax.inject.*

@ApiStatus.Internal
@CursedInternalApi
internal open class DefaultCursedRelationProject @Inject constructor(
    private val name: String,
    objects: ObjectFactory
) : CursedRelationProject {
    @get:Input
    override val type: Property<CursedRelationProject.RelationType> = objects.property()

    @Input
    override fun getName(): String = name

    override fun toString() = "DefaultCursedRelationProject(name=$name)"
}