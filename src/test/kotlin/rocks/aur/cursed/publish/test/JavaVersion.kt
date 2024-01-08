package rocks.aur.cursed.publish.test

import java.nio.file.*
import kotlin.io.path.*

data class JavaVersion(
    val version: Int,
    val path: Path
) {
    override fun toString() = "Java $version"

    companion object {
        fun of(version: Int): JavaVersion {
            val pathString = System.getProperty("cursed.publish.java.$version")
                ?: throw IllegalStateException("No java installation for version $version")
            val path = Paths.get(pathString)
            if (!path.isDirectory()) {
                throw IllegalStateException("Provided java installation for version $version isn't valid")
            }
            return JavaVersion(version, path)
        }
    }
}