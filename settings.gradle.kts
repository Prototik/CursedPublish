import com.mooltiverse.oss.nyx.gradle.*

plugins {
    id("com.gradle.enterprise") version "3.16.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
    id("com.mooltiverse.oss.nyx") version "2.5.2"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "cursedpublish"

include(":testlib")

extensions.configure<NyxExtension>("nyx") {
    configurationFile = ".nyx.yml"
    resume = System.getenv("NYX_RESUME") == "true"
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
