package rocks.aur.cursedpublish

import org.gradle.api.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.gradle.util.internal.*
import rocks.aur.cursedpublish.internal.*
import javax.inject.*

@Suppress("unused")
@OptIn(CursedInternalApi::class)
abstract class CursedPublishPlugin : Plugin<Project> {
    @get:Inject
    abstract val providers: ProviderFactory

    override fun apply(project: Project) {
        val uploadAllTask: TaskProvider<out Task> = project.tasks.register<Task>("curseforgeUploadAllFiles") {
            group = "upload"
            description = "Uploads all CurseForge projects"
        }

        val extension = project.extensions.create(
            CursedPublishExtension::class,
            CursedPublishExtension.NAME,
            DefaultCursedPublishExtension::class,
            uploadAllTask
        ) as DefaultCursedPublishExtension

        extension.files.whenObjectAdded {
            project.tasks.register<CursedPublishTask>(publishFileTaskName(name)) {
                group = "upload"
                description = "Uploads \"${this@whenObjectAdded.name}\" file"
                this.file.set(this@whenObjectAdded)
                this.file.finalizeValue()
                this.infer.convention(providers.provider { extension.globalInfer })
            }.also { provider ->
                uploadAllTask.configure {
                    dependsOn(provider)
                }
            }
        }
    }

    private companion object {
        @JvmStatic
        private fun publishFileTaskName(file: String): String {
            return GUtil.toLowerCamelCase("curseforge upload $file file")
        }
    }
}