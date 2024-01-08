package rocks.aur.cursed.publish.test.models

import io.kotest.core.spec.style.*
import io.kotest.matchers.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import rocks.aur.cursed.publish.*
import rocks.aur.cursed.publish.internal.*
import rocks.aur.cursed.publish.internal.model.*
import java.io.*

@OptIn(ExperimentalSerializationApi::class, CursedInternalApi::class)
object GameVersionTypesSpec : FunSpec({
    test("serialization") {
        val types = GameVersionTypesSpec.stream().use { stream ->
            CursedJson.decodeFromStream<JsonArray>(stream)
        }

        val deserialized = CursedJson.decodeFromJsonElement<List<GameVersionType>>(types)
        val serialized = CursedJson.encodeToJsonElement(deserialized)

        serialized shouldBe types
    }
}) {
    fun stream(): InputStream = GameVersionTypesSpec::class.java.getResourceAsStream("version-types.json")
        ?: throw AssertionError("no version-types.json was found")
}