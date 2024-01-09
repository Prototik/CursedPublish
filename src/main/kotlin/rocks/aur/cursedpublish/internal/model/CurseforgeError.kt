package rocks.aur.cursedpublish.internal.model

import kotlinx.serialization.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*

@Serializable
@ApiStatus.Internal
@CursedInternalApi
data class CurseforgeError(
    @SerialName("errorCode")
    val code: Int,
    @SerialName("errorMessage")
    val message: String
)