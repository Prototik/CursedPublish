package rocks.aur.cursedpublish

import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import rocks.aur.cursedpublish.internal.*

@OptIn(CursedInternalApi::class)
class CursedPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(
            CursedPublishExtension::class,
            CursedPublishExtension.NAME,
            DefaultCursedPublishExtension::class,
            project.tasks
        )
    }
}