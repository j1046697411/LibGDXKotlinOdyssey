import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    id("cn.jzl.kotlin-quality-conventions")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

// Provide a stable :module:test task for Kotlin Multiplatform modules.
// In KMP, the default test tasks are per-target (e.g., desktopTest). Some gates expect `test`.
tasks.matching { it.name == "test" }.configureEach {
    // If the plugin already created `test`, keep it.
}

val desktopTest = tasks.named("desktopTest")

val testAlias = tasks.register("test") {
    group = "verification"
    description = "Alias for desktopTest to make gates consistent across modules."
    dependsOn(desktopTest)
}

// Ensure JUnit Platform for desktop tests.
kotlin {
    targets.named<KotlinJvmTarget>("desktop") {
        tasks.named<Test>("desktopTest") {
            useJUnitPlatform()
            // Keep existing ByteBuddy workaround; don't force it on other targets.
            jvmArgs("-Dnet.bytebuddy.experimental=true")
        }
    }
}

// Coverage: only enforce verify on lko-sect module, and only for v2 packages.
// We wire the verify task name but only apply gate dependency in root preCommit.

kover {
    reports {
        filters {
            includes {
                // Kotlin class names are derived from package/class, not file paths.
                // This matches all classes under cn.jzl.sect.v2.
                classes("cn.jzl.sect.v2.*")
            }
        }
        verify {
            rule {
                // Line coverage >= 60%
                bound {
                    minValue = 60
                    metric = kotlinx.kover.gradle.plugin.dsl.MetricType.LINE
                    aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                }
            }
        }
    }
}

