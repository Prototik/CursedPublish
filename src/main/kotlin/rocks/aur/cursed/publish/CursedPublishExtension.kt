package rocks.aur.cursed.publish

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.jetbrains.annotations.*

@ApiStatus.NonExtendable
@CursedDsl
@SubclassOptInRequired(markerClass = CursedInternalApi::class)
interface CursedPublishExtension : CursedPublishSpec {
    val uploadAllTask: TaskProvider<out Task>

    companion object {
        const val NAME = "cursedPublish"
    }
}