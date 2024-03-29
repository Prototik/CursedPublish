package rocks.aur.cursedpublish

import kotlinx.serialization.*

enum class CursedReleaseType {
    @SerialName("release")
    Release,

    @SerialName("beta")
    Beta,

    @SerialName("alpha")
    Alpha,
}