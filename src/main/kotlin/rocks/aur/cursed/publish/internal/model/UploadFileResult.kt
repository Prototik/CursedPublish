package rocks.aur.cursed.publish.internal.model

import kotlinx.serialization.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*

@Serializable
@ApiStatus.Internal
@CursedInternalApi
internal data class UploadFileResult(
    val id: UInt
)