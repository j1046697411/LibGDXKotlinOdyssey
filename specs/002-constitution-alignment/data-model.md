# Data Model — Constitution Alignment (Governance artifact)

> This feature is primarily governance/build alignment rather than product-domain modeling.
> We still model a minimal set of *tracking entities* to make exceptions/audits explicit.

## Entity: ConstitutionAudit

Represents an audit of a concrete module/scope against constitution rules.

- `id: String` (e.g., `lko-sect-v2-2025-12`)
- `scope: String` (e.g., `lko-sect:v2`)
- `date: LocalDate`
- `rulesEvaluated: Set<ConstitutionRuleKey>`
- `findings: List<AuditFinding>`

## Entity: AuditFinding

- `rule: ConstitutionRuleKey`
- `type: FindingType` (`VIOLATION` | `EXCEPTION` | `OK`)
- `location: String` (file/package/gradle task)
- `description: String`
- `rationale: String?` (required for `EXCEPTION`)
- `remediationPlan: String?` (required for `EXCEPTION`)

## Enum: ConstitutionRuleKey

- `ECS_FIRST`
- `ADDON_DI`
- `DETekt_ZERO_ISSUES`
- `KTLINT_CLEAN`
- `TESTS_PASS`
- `COVERAGE_THRESHOLD`
- `DOCS_UP_TO_DATE`

## Validation rules derived from requirements

- Any `EXCEPTION` MUST include `rationale` and `remediationPlan` (maps to R-3.3).
- Coverage threshold is enforced only for `lko-sect:v2` scope in this iteration (maps to R-4.1).


