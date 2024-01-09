package rocks.aur.cursedpublish.internal.model

import kotlinx.serialization.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*

@Serializable
@ApiStatus.Internal
@CursedInternalApi
data class GameVersionType(
    val id: UInt,
    val name: String,
    val slug: String,
)
