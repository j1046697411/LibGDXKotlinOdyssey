plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    jvm("desktop")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting

        commonTest.dependencies {

            implementation(libs.kotlin.test)
        }

        commonMain.dependencies {
            api(projects.lkoEcs)
            api(libs.jetbrains.compose.runtime)
            api(libs.kotlinx.coroutines.core)
        }
        desktopMain.dependencies {
            implementation(libs.gdx.core)
            implementation(libs.gdx.ktx.log)
        }
    }
}

