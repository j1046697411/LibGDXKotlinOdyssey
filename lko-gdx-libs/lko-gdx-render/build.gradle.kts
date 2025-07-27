plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    api(libs.gdx.core)
    api(projects.lkoGraphCommon)

    implementation(libs.gdx.ktx.log)
}

