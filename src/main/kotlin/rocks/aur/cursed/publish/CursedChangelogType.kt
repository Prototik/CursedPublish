package rocks.aur.cursed.publish

import kotlinx.serialization.*

@Serializable
enum class CursedChangelogType {
    @SerialName("text")
    Text,

    @SerialName("html")
    Html,

    @SerialName("markdown")
    Markdown,
}