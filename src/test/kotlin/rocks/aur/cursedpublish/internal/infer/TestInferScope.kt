package rocks.aur.cursedpublish.internal.infer

import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.*
import rocks.aur.cursedpublish.internal.model.*
import rocks.aur.cursedpublish.testlib.models.*

@OptIn(CursedInternalApi::class)
object TestInferScope : Infer.Scope {
    override val versionTypes: Collection<GameVersionType> by lazy {
        ModelFixtures.gameVersionTypes().reader().use { reader ->
            CursedJson.decodeFromString(reader.readText())
        }
    }

    override val versions: Collection<GameVersion> by lazy {
        ModelFixtures.gameVersions().reader().use { reader ->
            CursedJson.decodeFromString(reader.readText())
        }
    }

    override val minecraftVersions: Collection<GameVersion> by lazy { super.minecraftVersions }
    override val environment: Collection<GameVersion> by lazy { super.environment }
    override val javaVersions: Collection<GameVersion> by lazy { super.javaVersions }
    override val modloaders: Collection<GameVersion> by lazy { super.modloaders }
}