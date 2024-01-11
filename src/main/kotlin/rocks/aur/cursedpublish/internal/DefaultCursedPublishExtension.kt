package rocks.aur.cursedpublish.internal

import org.gradle.api.*
import org.gradle.api.model.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.infer.*
import javax.inject.*

@ApiStatus.Internal
@CursedInternalApi
internal open class DefaultCursedPublishExtension @Inject constructor(
    override val uploadAllTask: TaskProvider<out Task>,
    objects: ObjectFactory,
) : DefaultCursedPublishSpec(objects), CursedPublishExtension {
    val infers: NamedDomainObjectSet<Infer> = objects.namedDomainObjectSet(Infer::class)

    init {
        infers.add(FabricModInfer)
    }

    val globalInfer: Infer = CompositeInfer(infers)

    override fun toString() = "CursedPublishExtension"
}