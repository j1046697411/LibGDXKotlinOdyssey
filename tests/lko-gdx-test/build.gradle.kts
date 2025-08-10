plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    api(libs.gdx.core)
    api(projects.lkoGraphCommon)
    api(projects.lkoGdxRender)
    api(projects.lkoGdxShader)
    api(projects.lkoGdxUi)

    implementation(libs.gdx.ktx.log)
    implementation(libs.gdx.ktx.collections)
    implementation(libs.gdx.ktx.app)
    implementation(libs.gdx.ktx.math)
    implementation(libs.gdx.ktx.vis)
    implementation(libs.gdx.ktx.scene2d)
    implementation(libs.gdx.ktx.style)
    implementation(libs.gdx.vis)

    implementation(libs.gdx.core)
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.13.1")
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-desktop")

}

