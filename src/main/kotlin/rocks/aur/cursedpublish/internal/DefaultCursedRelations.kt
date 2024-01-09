package rocks.aur.cursedpublish.internal

import org.gradle.api.*
import org.gradle.api.model.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import javax.inject.*

@ApiStatus.Internal
@CursedInternalApi
internal open class DefaultCursedRelations @Inject constructor(
    objects: ObjectFactory
) : CursedRelations {
    override val projects: NamedDomainObjectContainer<DefaultCursedRelationProject> =
        objects.domainObjectContainer(DefaultCursedRelationProject::class)

    override fun toString() = "CursedRelations"
}