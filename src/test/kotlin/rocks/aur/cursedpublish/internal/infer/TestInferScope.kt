package rocks.aur.cursedpublish.internal.infer

import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.*
import rocks.aur.cursedpublish.internal.model.*
import rocks.aur.cursedpublish.testlib.models.*

@OptIn(CursedInternalApi::class)
internal object TestInferScope : Infer.Scope() {
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
}