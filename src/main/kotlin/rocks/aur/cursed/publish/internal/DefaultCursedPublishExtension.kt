package rocks.aur.cursed.publish.internal

import org.gradle.api.*
import org.gradle.api.model.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.gradle.util.internal.*
import org.jetbrains.annotations.*
import rocks.aur.cursed.publish.*
import javax.inject.*

@ApiStatus.Internal
@CursedInternalApi
internal open class DefaultCursedPublishExtension @Inject constructor(
    private val tasks: TaskContainer,
    objects: ObjectFactory,
) : DefaultCursedPublishSpec(objects), CursedPublishExtension {
    override val uploadAllTask: TaskProvider<out Task> = tasks.register<Task>("curseforgeUploadAllFiles") {
        group = "upload"
        description = "Uploads all CurseForge projects"
    }

    init {
        files.whenObjectAdded {
            val file = this
            this@DefaultCursedPublishExtension.tasks.register<CursedPublishTask>(publishFileTaskName(file.name)) {
                group = "upload"
                description = "Uploads \"${file.name}\" file"
                this.file.set(file)
                this.file.finalizeValue()
            }.also { provider ->
                this@DefaultCursedPublishExtension.uploadAllTask.configure {
                    dependsOn(provider)
                }
            }
        }
    }

    override fun toString() = "CursedPublishExtension"

    companion object {
        @JvmStatic
        fun publishFileTaskName(file: String): String {
            return GUtil.toLowerCamelCase("curseforge upload $file file")
        }
    }
}