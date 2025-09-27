import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins { alias(libs.plugins.kotlinMultiplatform) }

kotlin {

    jvm("desktop")

    sourceSets {
        val commonMain by getting
        val desktopMain by getting
        commonMain.dependencies {
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test.junit)
            implementation(libs.junit.jupiter.api)
            implementation(libs.junit.jupiter.engine)
            implementation(libs.mockk)
        }
    }

    // 配置desktopTest任务使用JUnit Platform
    targets.named<KotlinJvmTarget>("desktop") {
        tasks.named<Test>("desktopTest") {
            useJUnitPlatform()
            // 添加JVM参数以启用Byte Buddy的实验性Java 23支持
            jvmArgs("-Dnet.bytebuddy.experimental=true")
        }
    }
}

