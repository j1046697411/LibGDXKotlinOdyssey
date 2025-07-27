plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    api(libs.gdx.core)
    api(projects.lkoGraphCommon)
    api(projects.lkoGdxRender)

    implementation(libs.gdx.ktx.log)
    implementation(libs.gdx.ktx.collections)
}

