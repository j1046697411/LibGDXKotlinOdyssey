plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    jvm("desktop")
    js("browser")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting

        commonMain.dependencies {
            api(projects.lkoEcs4)
            implementation(libs.kotlinx.serialization.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
        }
    }
}

