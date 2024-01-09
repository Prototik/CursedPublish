package rocks.aur.cursedpublish.internal.model

import kotlinx.serialization.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*

@Serializable
@ApiStatus.Internal
@CursedInternalApi
internal data class UploadFileResult(
    val id: UInt
)