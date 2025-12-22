plugins {
    // Root-only "entrypoint" tasks.
}

// Canonical gate entrypoint.
// Keep this at root so CI/docs can always call `./gradlew preCommit`.
tasks.register("preCommit") {
    group = "verification"
    description = "Canonical local=CI quality gate entrypoint for the whole repo."

    // Run format/lint/tests across all included modules that have these tasks.
    // Using task paths makes dependencies explicit.
    // NOTE: this repo currently scopes gates to :lko-sect (+ its strong deps via regular task graph).
    dependsOn(
        ":lko-sect:ktlintFormat",
        ":lko-sect:ktlintCheck",
        ":lko-sect:detekt",
        ":lko-sect:test",
        // coverage merge blocker for v2
        ":lko-sect:koverVerify",
        ":lko-sect:check"
    )
}

