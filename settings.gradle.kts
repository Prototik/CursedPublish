plugins {
    id("com.gradle.enterprise") version "3.16.1"
}

rootProject.name = "cursed-publish"

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}