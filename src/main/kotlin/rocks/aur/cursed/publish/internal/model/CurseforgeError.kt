package rocks.aur.cursed.publish.internal.model

import kotlinx.serialization.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*

@Serializable
@ApiStatus.Internal
@CursedInternalApi
data class CurseforgeError(
    @SerialName("errorCode")
    val code: Int,
    @SerialName("errorMessage")
    val message: String
)