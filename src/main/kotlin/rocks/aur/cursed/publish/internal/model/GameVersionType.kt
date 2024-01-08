package rocks.aur.cursed.publish.internal.model

import kotlinx.serialization.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*

@Serializable
@ApiStatus.Internal
@CursedInternalApi
data class GameVersionType(
    val id: UInt,
    val name: String,
    val slug: String,
)
