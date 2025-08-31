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
            api(projects.lkoMath)

            api(libs.compose.runtime)

            api(libs.kotlinx.coroutines.core)
            api(libs.bundles.korlibs.all)
        }
        desktopMain.dependencies {
            implementation(libs.gdx.core)
            implementation(libs.gdx.ktx.log)
        }
    }
}

