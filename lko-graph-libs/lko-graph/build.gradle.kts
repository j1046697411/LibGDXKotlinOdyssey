plugins { alias(libs.plugins.kotlinMultiplatform) }

kotlin {

    jvm("desktop")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting

        commonMain.dependencies {
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
        }
    }
}

