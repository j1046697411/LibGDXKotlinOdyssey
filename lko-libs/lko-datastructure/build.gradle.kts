plugins { alias(libs.plugins.kotlinMultiplatform) }

kotlin {

    jvm("desktop")
    js("browser")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting

        commonMain.dependencies {
            implementation(libs.kotlinx.atomics)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

