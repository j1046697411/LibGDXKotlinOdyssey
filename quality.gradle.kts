/**
 * Quality assurance plugins and configuration for lko-sect module.
 * This file extends the main build.gradle.kts with code quality checks.
 *
 * Apply with: apply from: "$rootDir/quality.gradle.kts"
 */

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("jacoco")
}

// KtLint configuration
ktlint {
    version.set("1.0.1")
    enableExperimentalRules.set(true)
    ignoreFailures.set(false)

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

// Detekt configuration (static analysis)
detekt {
    toolVersion = "1.23.4"
    config = files("detekt.yml")
    source.setFrom("src/commonMain/kotlin", "src/desktopMain/kotlin")
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

tasks.detekt {
    dependsOn.clear()
    setSource(SourceTask::getSource.map { it })
}

// JaCoCo configuration (code coverage)
jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/generated/**",
                    "**/*Test**",
                    "**/*Config**"
                )
            }
        }))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            includes = listOf("cn.jzl.sect.*")

            limit {
                minimum = "0.75".toBigDecimal()
            }
        }

        rule {
            element = "SOURCEFILE"
            includes = listOf("cn/jzl/sect/ecs/**")

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

// Add quality check task
tasks.register("qualityCheck") {
    dependsOn(
        "ktlintCheck",
        "detekt",
        "test",
        "jacocoTestReport",
        "jacocoTestCoverageVerification"
    )

    doLast {
        println("✓ Quality checks passed!")
        println("  - KtLint: Code style verified")
        println("  - Detekt: Static analysis completed")
        println("  - JUnit: All tests passed")
        println("  - JaCoCo: Coverage verified (≥75%)")
    }
}

// Before merge checks
tasks.register("preCommit") {
    dependsOn(
        "ktlintFormat",
        "qualityCheck"
    )

    doLast {
        println("✓ Pre-commit checks completed!")
        println("  Ready to commit and push")
    }
}

