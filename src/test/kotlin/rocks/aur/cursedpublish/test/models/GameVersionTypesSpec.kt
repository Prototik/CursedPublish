package rocks.aur.cursedpublish.test.models

import io.kotest.core.spec.style.*
import io.kotest.matchers.*
import kotlinx.serialization.json.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.*
import rocks.aur.cursedpublish.internal.model.*
import rocks.aur.cursedpublish.testlib.models.*

@OptIn(CursedInternalApi::class)
object GameVersionTypesSpec : FunSpec({
    test("serialization") {
        val types = ModelFixtures.gameVersionTypes().use { stream ->
            CursedJson.decodeFromString<JsonArray>(stream.bufferedReader().readText())
        }

        val deserialized = CursedJson.decodeFromJsonElement<List<GameVersionType>>(types)
        val serialized = CursedJson.encodeToJsonElement(deserialized)

        serialized shouldBe types
    }
})