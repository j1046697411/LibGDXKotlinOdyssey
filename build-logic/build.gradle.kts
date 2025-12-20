plugins {
    `kotlin-dsl`
}

group = "cn.jzl.buildlogic"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jlleitschuh.gradle:ktlint-gradle:11.6.1")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.4")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.8.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
}

