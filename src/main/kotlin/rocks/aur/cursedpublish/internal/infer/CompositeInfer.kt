package rocks.aur.cursedpublish.internal.infer

import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.model.*

@ApiStatus.Internal
@CursedInternalApi
internal class CompositeInfer(internal val infers: Collection<Infer>) : Infer {
    override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> = buildSet {
        infers.forEach { infer ->
            with(infer) {
                addAll(inferGameVersions(file))
            }
        }
    }
}