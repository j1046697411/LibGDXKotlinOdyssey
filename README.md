# LibGDXKotlinOdyssey

This repository is a Kotlin multi-module workspace.

## Quality gates (local = CI)

Canonical entrypoint:

```bash
./gradlew preCommit
```

Fast iteration for SECT v2:

```bash
./gradlew :lko-sect:check
./gradlew :lko-sect:ktlintCheck :lko-sect:detekt
./gradlew :lko-sect:test
./gradlew :lko-sect:koverHtmlReport :lko-sect:koverVerify
```

Coverage merge blocker (this feature):

- Scope: `cn.jzl.sect.v2.*`
- Metric: LINE covered percentage
- Threshold: >= 60%

