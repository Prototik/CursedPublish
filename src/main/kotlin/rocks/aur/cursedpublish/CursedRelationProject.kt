package rocks.aur.cursedpublish

import kotlinx.serialization.*
import org.gradle.api.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.jetbrains.annotations.*

@ApiStatus.NonExtendable
@CursedDsl
@SubclassOptInRequired(markerClass = CursedInternalApi::class)
interface CursedRelationProject : Named {
    @Input
    override fun getName(): String

    @get:Input
    val type: Property<RelationType>

    fun type(type: RelationType) {
        this.type.set(type)
    }

    @Serializable
    enum class RelationType {
        @SerialName("embeddedLibrary")
        EmbeddedLibrary,

        @SerialName("incompatible")
        Incompatible,

        @SerialName("optionalDependency")
        OptionalDependency,

        @SerialName("requiredDependency")
        RequiredDependency,

        @SerialName("tool")
        Tool,
    }
}