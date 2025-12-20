import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    id("cn.jzl.kotlin-quality-conventions")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

// Provide a stable :module:test task for Kotlin Multiplatform modules.
// In KMP, the default test tasks are per-target (e.g., desktopTest). Some gates expect `test`.
// Not every module has a desktop target/task, so guard this wiring.
val desktopTestTask = tasks.matching { it.name == "desktopTest" }

if (!desktopTestTask.isEmpty()) {
    val desktopTest = tasks.named("desktopTest")

    // If the plugin already created `test`, keep it.
    // Otherwise, create an alias task.
    if (tasks.names.contains("test")) {
        tasks.named("test").configure {
            dependsOn(desktopTest)
        }
    } else {
        tasks.register("test") {
            group = "verification"
            description = "Alias for desktopTest to make gates consistent across modules."
            dependsOn(desktopTest)
        }
    }
}

// Ensure JUnit Platform for desktop tests (only when a desktop target exists).
kotlin {
    targets.withType<KotlinJvmTarget>().matching { it.name == "desktop" }.configureEach {
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
                    // Kover 0.8.x DSL
                    coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
                    aggregationForGroup = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                }
            }
        }
    }
}
