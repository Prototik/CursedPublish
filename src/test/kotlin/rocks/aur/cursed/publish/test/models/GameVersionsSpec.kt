package rocks.aur.cursed.publish.test.models

import io.kotest.core.spec.style.*
import io.kotest.matchers.*
import kotlinx.serialization.json.*
import rocks.aur.cursed.publish.*
import rocks.aur.cursed.publish.internal.*
import rocks.aur.cursed.publish.internal.model.*

@OptIn(CursedInternalApi::class)
object GameVersionsSpec : FunSpec({
    test("serialization") {
        val types = JsonArray(ModelFixtures.gameVersions().use { stream ->
            CursedJson.decodeFromString<JsonArray>(stream.bufferedReader().readText())
        }.map {
            when (it) {
                is JsonObject -> JsonObject(it - "apiVersion")
                else -> it
            }
        })

        val deserialized = CursedJson.decodeFromJsonElement<List<GameVersion>>(types)
        val serialized = CursedJson.encodeToJsonElement(deserialized)

        serialized shouldBe types
    }
})