package rocks.aur.cursedpublish.internal

import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.jetbrains.annotations.*
import rocks.aur.cursedpublish.*

@CursedInternalApi
@ApiStatus.Internal
internal val CursedJson = Json(DefaultJson) {
    ignoreUnknownKeys = true
}