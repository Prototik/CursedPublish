package rocks.aur.cursed.publish.test.models

import io.kotest.core.spec.style.*
import io.kotest.matchers.*
import kotlinx.serialization.json.*
import rocks.aur.cursed.publish.*
import rocks.aur.cursed.publish.internal.*
import rocks.aur.cursed.publish.internal.model.*

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