plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    api(libs.gdx.core)
    api(projects.lkoGraphCommon)

    implementation(libs.gdx.vis)
    implementation(libs.gdx.ktx.vis)
    implementation(libs.gdx.ktx.style)
    implementation(libs.gdx.ktx.vis.style)
    implementation(libs.gdx.ktx.log)
    implementation(libs.gdx.ktx.collections)
}

