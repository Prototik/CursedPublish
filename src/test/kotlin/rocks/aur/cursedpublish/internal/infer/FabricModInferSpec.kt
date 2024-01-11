@file:OptIn(CursedInternalApi::class)

package rocks.aur.cursedpublish.internal.infer

import io.github.z4kn4fein.semver.*
import io.kotest.core.spec.style.*
import io.kotest.datatest.*
import io.kotest.engine.spec.*
import io.kotest.matchers.collections.*
import kotlinx.serialization.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.*
import java.io.*
import java.util.jar.*

object FabricModInferSpec : FunSpec({
    val modFile: File = tempfile()

    fun infer() = with(FabricModInfer) {
        TestInferScope.inferGameVersions(modFile)
    }

    test("should infer fabric loader") {
        genDummyFabricMod(modFile)

        infer().filter { it in TestInferScope.modloaders }
            .map { it.slug } shouldContainExactlyInAnyOrder listOf("fabric")
    }

    context("should infer exact minecraft version") {
        withData("1.16.5", "1.18.2", "1.20.2") { minecraftVersion ->
            genDummyFabricMod(
                modFile,
                minecraftVersion = minecraftVersion,
                minecraftVersionConstraint = "=$minecraftVersion"
            )

            infer().filter { it in TestInferScope.minecraftVersions }
                .map { it.name } shouldContainExactlyInAnyOrder listOf(minecraftVersion)
        }
    }

    context("should infer minecraft version range") {
        @IsStableType
        data class TestData(
            val version: String,
            val constraint: String,
            val result: Collection<String>
        )

        withData(
            TestData("1.18", "~1.18", listOf("1.18", "1.18.1", "1.18.2")),
            TestData("1.18.1", "~1.18.1", listOf("1.18.1", "1.18.2")),
            TestData("1.18.2", "~1.18.2", listOf("1.18.2")),
            TestData("1.18.2", ">=1.18.2 <1.19", listOf("1.18.2")),
            TestData("1.18.2", ">=1.18.2 <=1.19.2", listOf("1.18.2", "1.19", "1.19.1", "1.19.2")),
        ) { (minecraftVersion, minecraftVersionConstraint, result) ->
            genDummyFabricMod(
                modFile,
                minecraftVersion = minecraftVersion,
                minecraftVersionConstraint = minecraftVersionConstraint
            )

            infer().filter { it in TestInferScope.minecraftVersions }
                .map { it.name } shouldContainExactlyInAnyOrder result
        }
    }

    context("should infer environment") {
        @IsStableType
        data class TestData(
            val inputs: Set<FabricModInfer.Environment>,
            val result: Set<String>
        )

        withData(
            TestData(setOf(FabricModInfer.Environment.Client), setOf("client")),
            TestData(setOf(FabricModInfer.Environment.Server), setOf("server")),
            TestData(FabricModInfer.Environment.ALL, setOf("server", "client"))
        ) { (inputs, result) ->
            genDummyFabricMod(
                modFile,
                environment = inputs,
            )

            infer().filter { it in TestInferScope.environment }
                .map { it.slug } shouldContainExactlyInAnyOrder result
        }
    }
})

private fun genDummyFabricMod(
    file: File,
    modId: String = "dummy",
    modVersion: Version = Version.parse("1.2.3"),
    minecraftVersion: String = "1.20.4",
    minecraftVersionConstraint: String = "~$minecraftVersion",
    fabricLoaderVersion: String = "0.15.0",
    fabricLoaderVersionConstraint: String = ">=$fabricLoaderVersion",
    environment: Set<FabricModInfer.Environment> = FabricModInfer.Environment.ALL,
) {
    val manifest = Manifest()
    manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
    manifest.mainAttributes.putValue("Fabric-Minecraft-Version", minecraftVersion)
    manifest.mainAttributes.putValue("Fabric-Loader-Version", fabricLoaderVersion)

    val modInfo = FabricModInfer.FabricModInfo(
        schemaVersion = 1,
        id = modId,
        version = modVersion,
        depends = mapOf(
            "minecraft" to FabricModInfer.VersionRange(minecraftVersionConstraint),
            "fabricloader" to FabricModInfer.VersionRange(fabricLoaderVersionConstraint),
        ),
        environment = environment
    )

    JarOutputStream(file.outputStream(), manifest).use { jar ->
        val writer = jar.writer()
        jar.putNextEntry(JarEntry("fabric.mod.json"))
        writer.write(CursedJsonPretty.encodeToString(modInfo))
        writer.flush()
        jar.closeEntry()
    }
}

