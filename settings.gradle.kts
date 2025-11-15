@file:Suppress("UnstableApiUsage")

rootProject.name = "LibGDXKotlinOdyssey"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/apache-snapshots")
        maven("https://s01.oss.sonatype.org")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jitpack.io")
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/apache-snapshots")
        maven("https://s01.oss.sonatype.org")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jitpack.io")
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

addPluginLibs(File(rootDir, "lko-libs/lko-datastructure"))
addPluginLibs(File(rootDir, "lko-libs/lko-di"))
addPluginLibs(File(rootDir, "lko-libs/lko-ecs4"))


//addPluginLibs(File(rootDir, "lko-libs"))
//addPluginLibs(File(rootDir, "lko-graph-libs"))
//addPluginLibs(File(rootDir, "lko-gdx-libs"))
//addPluginLibs(File(rootDir, "tests"))

fun addPluginLibs(libs: File) {
    if (!libs.isDirectory) return
    if (File(libs, "build.gradle.kts").exists()) {
        include(":${libs.name}")
        val project = project(":${libs.name}")
        project.projectDir = libs
        project.name = libs.name

        println("$libs ${libs.name}")
        return
    }
    libs.listFiles().forEach(::addPluginLibs)
}