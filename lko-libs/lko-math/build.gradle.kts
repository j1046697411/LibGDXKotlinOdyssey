plugins { alias(libs.plugins.kotlinMultiplatform) }

kotlin {

    jvm("desktop")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting

        commonMain.dependencies {
            api(projects.lkoDatastructure)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

