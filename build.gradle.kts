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