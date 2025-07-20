plugins { alias(libs.plugins.kotlinMultiplatform) }

kotlin {

    jvm("desktop")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        commonMain.dependencies {
            api(libs.kodein.kaverit)
        }
        desktopMain.dependencies {
        }
    }
}

