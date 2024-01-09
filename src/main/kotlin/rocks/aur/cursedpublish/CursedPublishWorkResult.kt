package rocks.aur.cursedpublish

import org.gradle.api.tasks.*
import org.jetbrains.annotations.*
import java.util.Optional

@ApiStatus.NonExtendable
@SubclassOptInRequired(markerClass = CursedInternalApi::class)
interface CursedPublishWorkResult : WorkResult {
    val fileId: UInt

    val file: Optional<out CursedFile>

    val nested: Collection<CursedPublishWorkResult>
}