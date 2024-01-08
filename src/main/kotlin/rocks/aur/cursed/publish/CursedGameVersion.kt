package rocks.aur.cursed.publish

import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.jetbrains.annotations.*

@ApiStatus.NonExtendable
@CursedDsl
@SubclassOptInRequired(markerClass = CursedInternalApi::class)
interface CursedGameVersion {
    @ApiStatus.NonExtendable
    @CursedDsl
    interface Minecraft : CursedGameVersion {
        @get:Input
        val version: Property<String>
    }

    @ApiStatus.NonExtendable
    @CursedDsl
    interface ModLoader : CursedGameVersion {
        @get:Input
        val type: Property<Type>

        enum class Type(
            @property:CursedInternalApi
            @ApiStatus.Internal
            internal val slug: String,
        ) {
            Forge("forge"),
            NeoForge("neoforge"),
            Fabric("fabric"),
            Quilt("quilt"),
            Rift("rift"),
            RisugamisModLoader("risugamis-modloader"),
            ;
        }
    }

    @ApiStatus.NonExtendable
    @CursedDsl
    interface Java : CursedGameVersion {
        @get:Input
        val version: Property<Int>
    }

    @ApiStatus.NonExtendable
    @CursedDsl
    interface Environment : CursedGameVersion {
        @get:Input
        val type: Property<Type>

        enum class Type(
            @property:CursedInternalApi
            @ApiStatus.Internal
            internal val slug: String
        ) {
            Client("client"),
            Server("server"),
            ;
        }
    }
}