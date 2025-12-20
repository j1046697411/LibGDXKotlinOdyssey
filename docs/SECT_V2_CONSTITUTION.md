# SECT v2  Constitution alignment

This note documents *where to look* and *how to validate* the SECT v2 core paths.

## Core entrypoints (v2)

- Package scope: `cn.jzl.sect.v2`
- Planning / scheduling core (GOAP): `lko-sect/src/commonMain/kotlin/cn/jzl/sect/v2/Planning.kt`
- Item actions + state resolvers: `lko-sect/src/commonMain/kotlin/cn/jzl/sect/v2/ECS.kt`

## Quality gate entrypoints

- Repo canonical gate: `./gradlew preCommit`
- SECT module gate: `./gradlew :lko-sect:check`
- Coverage (merge blocker for this feature):
  - report: `./gradlew :lko-sect:koverHtmlReport`
  - verify: `./gradlew :lko-sect:koverVerify`

## Coverage contract (this feature)

- Scope: `cn.jzl.sect.v2.*`
- Metric: LINE covered percentage
- Threshold: >= 60%

## Testing expectations

SECT v2 tests should be deterministic and avoid time-based assertions.
Prefer pure unit tests for planning/scheduling and small smoke tests for wiring.

