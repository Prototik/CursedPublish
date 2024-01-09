@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    embeddedKotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:$embeddedKotlinVersion"))
    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(platform(libs.slf4j.bom))
    implementation(platform(libs.junit.bom))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.junit.jupiter:junit-jupiter")
    implementation(libs.bundles.kotest)
}