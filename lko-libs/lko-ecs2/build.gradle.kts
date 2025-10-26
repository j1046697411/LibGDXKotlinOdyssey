plugins { alias(libs.plugins.kotlinMultiplatform) }

kotlin {

    jvm("desktop")
    js("browser")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting

        commonMain.dependencies {
            api(projects.lkoDi)
            api(projects.lkoDatastructure)
            api(libs.kodein.kaverit)
            api(libs.kotlinx.atomics)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
        }
    }
}

