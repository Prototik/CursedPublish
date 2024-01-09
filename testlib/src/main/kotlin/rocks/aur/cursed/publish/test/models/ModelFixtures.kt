package rocks.aur.cursed.publish.test.models

import java.io.*

object ModelFixtures {
    fun gameVersionTypes(): InputStream = ModelFixtures::class.java.getResourceAsStream("version-types.json")
        ?: throw AssertionError("no version-types.json was found")

    fun gameVersions(): InputStream = ModelFixtures::class.java.getResourceAsStream("versions.json")
        ?: throw AssertionError("no versions.json was found")

}