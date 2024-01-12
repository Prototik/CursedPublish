package rocks.aur.cursedpublish.internal.infer

import io.github.z4kn4fein.semver.*
import io.github.z4kn4fein.semver.constraints.*
import kotlinx.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import org.gradle.api.*
import org.jetbrains.annotations.*
import org.slf4j.*
import rocks.aur.cursedpublish.*
import rocks.aur.cursedpublish.internal.*
import rocks.aur.cursedpublish.internal.model.*
import java.io.*
import java.util.jar.*

@ApiStatus.Internal
@CursedInternalApi
internal object FabricModInfer : Infer, Named {
    private val logger = LoggerFactory.getLogger(FabricModInfo::class.java)
    override fun getName(): String = "fabric"

    override fun Infer.Scope.inferGameVersions(file: CursedFile): Collection<GameVersion> {
        try {
            return inferGameVersions(file.file.asFile.get())
        } catch (e: Exception) {
            logger.error("Failed to process fabric infer", e)
            return emptyList()
        }
    }

    internal fun Infer.Scope.inferGameVersions(file: File): Collection<GameVersion> = buildSet {
        val jar = JarFile(file)
        try {
            jar.getJarEntry("fabric.mod.json")?.let { entry ->
                addAll(inferFromModInfo(jar.getInputStream(entry)))
            }
        } catch (e: Exception) {
            logger.error("Error occurred during inspection of fabric.mod.json", e)
        }
        try {
            jar.manifest?.let {
                addAll(inferFromManifest(it))
            }
        } catch (e: Exception) {
            logger.error("Error occurred during inspection of manifest", e)
        }
    }

    private fun Infer.Scope.inferFromModInfo(
        stream: InputStream
    ): Collection<GameVersion> {
        val json = stream.bufferedReader().use { reader ->
            CursedJson.parseToJsonElement(reader.readText())
        }

        return when (json) {
            is JsonArray -> json.flatMapTo(mutableSetOf()) {
                inferFromModInfo(CursedJson.decodeFromJsonElement<FabricModInfo>(it))
            }

            is JsonObject -> inferFromModInfo(CursedJson.decodeFromJsonElement<FabricModInfo>(json))
            else -> emptySet()
        }
    }

    private fun Infer.Scope.inferFromModInfo(
        modInfo: FabricModInfo
    ): Collection<GameVersion> = buildSet {
        this += modloaders.single { it.slug == "fabric" }

        modInfo.depends["minecraft"]?.let { minecraftRange ->
            this += minecraftVersions.filterValues {
                it != null && it.isStable && minecraftRange.constraint.isSatisfiedBy(it)
            }.keys
        }

        if (Environment.Client in modInfo.environment) {
            this += environment.single { it.slug == "client" }
        }
        if (Environment.Server in modInfo.environment) {
            this += environment.single { it.slug == "server" }
        }
    }

    private fun Infer.Scope.inferFromManifest(manifest: Manifest): Collection<GameVersion> = buildSet {
        manifest.mainAttributes.getValue("Fabric-Minecraft-Version")?.let { minecraftVersion ->
            val constraint = Constraint.parse("=$minecraftVersion")
            this += minecraftVersions.filterValues {
                it != null && constraint.isSatisfiedBy(it)
            }.keys
        }
        manifest.mainAttributes.getValue("Fabric-Loader-Version")?.let {
            this += modloaders.single { it.slug == "fabric" }
        }
    }

    @ApiStatus.Internal
    @CursedInternalApi
    @Serializable
    internal data class FabricModInfo(
        val schemaVersion: Int = 0,
        val id: String,
        val version: Version,
        @Serializable(with = Environment.SetSerializer::class)
        val environment: Set<Environment> = Environment.ALL,
        val depends: Map<String, VersionRange> = emptyMap(),
        val recommends: Map<String, VersionRange> = emptyMap(),
        val suggests: Map<String, VersionRange> = emptyMap(),
        val conflicts: Map<String, VersionRange> = emptyMap(),
        val breaks: Map<String, VersionRange> = emptyMap(),
    )

    @ApiStatus.Internal
    @CursedInternalApi
    @Serializable(with = VersionRange.Serializer::class)
    @JvmInline
    internal value class VersionRange(val constraint: Constraint) {
        constructor(constraint: String) : this(Constraint.parse(constraint))

        @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
        object Serializer : KSerializer<VersionRange> {
            override val descriptor: SerialDescriptor = buildSerialDescriptor(
                "rocks.aur.cursedpublish.internal.infer.FabricModInfer.VersionRange", PolymorphicKind.SEALED
            ) {
                element("constraint", serialDescriptor<Constraint>())
            }

            override fun deserialize(decoder: Decoder): VersionRange {
                check(decoder is JsonDecoder)
                val string = when (val json = decoder.decodeJsonElement()) {
                    is JsonArray -> json.joinToString(" || ") { it.jsonPrimitive.content }
                    is JsonPrimitive -> json.content
                    else -> throw SerializationException("Not supported json type: $json")
                }
                return VersionRange(Constraint.parse(string))
            }

            override fun serialize(encoder: Encoder, value: VersionRange) {
                check(encoder is JsonEncoder)
                val elements = value.constraint.toString().split('|').map { it.trim() }.filter { it.isNotBlank() }
                val json = when (elements.size) {
                    0 -> JsonPrimitive("")
                    1 -> JsonPrimitive(elements[0])
                    else -> JsonArray(elements.map { JsonPrimitive(it) })
                }
                encoder.encodeJsonElement(json)
            }
        }
    }

    @ApiStatus.Internal
    @CursedInternalApi
    @Serializable
    enum class Environment {
        @SerialName("client")
        Client,

        @SerialName("server")
        Server,
        ;

        companion object {
            val ALL = setOf(Client, Server)
        }

        @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
        object SetSerializer : KSerializer<Set<Environment>> {
            override val descriptor: SerialDescriptor = buildSerialDescriptor(
                "kotlin.collections.Set<rocks.aur.cursedpublish.internal.infer.FabricModInfer.Environment>",
                PolymorphicKind.SEALED
            ) {
                element("single", serialDescriptor<Environment>())
                element("multiple", serialDescriptor<Set<Environment>>())
            }

            override fun deserialize(decoder: Decoder): Set<Environment> {
                check(decoder is JsonDecoder)
                val json = decoder.decodeJsonElement()
                return when (json) {
                    is JsonArray -> decoder.json.decodeFromJsonElement<Set<String>>(json)
                    is JsonPrimitive -> setOf(decoder.json.decodeFromJsonElement<String>(json))
                    else -> throw SerializationException("Not supported type for environment set: $json")
                }.flatMapTo(mutableSetOf()) {
                    when (it) {
                        "*" -> ALL
                        "client" -> setOf(Client)
                        "server" -> setOf(Server)
                        else -> throw SerializationException("Not supported environment: $it")
                    }
                }
            }

            override fun serialize(encoder: Encoder, value: Set<Environment>) {
                when {
                    ALL == value || value.isEmpty() -> encoder.encodeString("*")
                    value.size == 1 -> encoder.encodeSerializableValue(Environment.serializer(), value.single())
                    else -> encoder.encodeSerializableValue(serializer<Set<Environment>>(), value)
                }
            }
        }
    }
}