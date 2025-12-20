buildscript {
    dependencies {
        classpath("com.kotcrab.vis:vis-usl:0.2.1")
        classpath("com.badlogicgames.gdx:gdx-tools:1.9.2")
    }
}

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
//    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("cn.jzl.root-quality-conventions")
}

// Use a consistent toolchain so Gradle can run without relying on system JAVA_HOME/PATH.
subprojects {
    // Configure Kotlin JVM toolchain where relevant (KMP/JVM).
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>("kotlin") {
            jvmToolchain(21)
        }
    }
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>("kotlin") {
            jvmToolchain(21)
        }
    }
}

// Root aggregator so `./gradlew test` works in this multi-module KMP setup.
val rootTest = tasks.register("test") {
    group = "verification"
    description = "Runs tests for all subprojects (delegates to :sub:test if present, otherwise :sub:desktopTest when available)."
}

gradle.projectsEvaluated {
    rootTest.configure {
        subprojects.forEach { sub ->
            val candidate = sub.tasks.findByName("test") ?: sub.tasks.findByName("desktopTest")
            if (candidate != null) {
                dependsOn(candidate)
            }
        }
    }
}
