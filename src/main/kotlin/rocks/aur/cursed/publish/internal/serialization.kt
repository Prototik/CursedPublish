package rocks.aur.cursed.publish.internal

import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*

@CursedInternalApi
@ApiStatus.Internal
internal val CursedJson = Json(DefaultJson) {
    ignoreUnknownKeys = true
}