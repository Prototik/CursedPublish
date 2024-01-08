package rocks.aur.cursed.publish.test.models

import io.kotest.core.spec.style.*
import io.kotest.matchers.*
import kotlinx.serialization.json.*
import rocks.aur.cursed.publish.*
import rocks.aur.cursed.publish.internal.*
import rocks.aur.cursed.publish.internal.model.*
import java.io.*

@OptIn(CursedInternalApi::class)
object GameVersionsSpec : FunSpec({
    test("serialization") {
        val types = JsonArray(GameVersionsSpec.stream().use { stream ->
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
}) {
    fun stream(): InputStream = GameVersionTypesSpec::class.java.getResourceAsStream("versions.json")
        ?: throw AssertionError("no versions.json was found")
}