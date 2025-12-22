rootProject.name = "build-logic"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        // Keep in sync with gradle/libs.versions.toml (kotlin = 2.0.0)
        id("org.jetbrains.kotlin.multiplatform") version "2.0.0"
        id("org.jetbrains.kotlin.jvm") version "2.0.0"
        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
