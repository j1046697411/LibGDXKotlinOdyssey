import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.Test

plugins {
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

// Keep reports in stable paths so CI can always pick them up.
val reportsDir = layout.buildDirectory.dir("reports")

ktlint {
    version.set("1.0.1")
    enableExperimentalRules.set(true)
    ignoreFailures.set(false)

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
    }
}

detekt {
    toolVersion = "1.23.4"
    config.setFrom(rootProject.files("detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false

    // default output dir; individual tasks set formats below
    basePath = rootProject.projectDir.absolutePath
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

// Convenience gates. They only point at existing tasks; module-specific wiring (like MPP test alias)
// should be provided by the module convention plugin.
tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs formatting, static analysis and tests.".
        trim()

    dependsOn("ktlintCheck", "detekt", "test")
}

tasks.register("preCommit") {
    group = "verification"
    description = "Canonical local=CI quality gate entrypoint.".
        trim()

    // Format + all checks
    dependsOn("ktlintFormat", "qualityCheck")
}

