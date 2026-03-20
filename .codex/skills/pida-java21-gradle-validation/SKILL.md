---
name: pida-java21-gradle-validation
description: Use for validating or triaging build, test, lint, and JDK issues in PIDA. Covers Java 21, Gradle module selection, ktlint hook behavior, and CI parity for the core-api build.
metadata:
  short-description: Validate PIDA builds with Java 21
---

# PIDA Java 21 Gradle Validation

Use this skill when you need to choose the right validation command for a PIDA change or explain why local and CI behavior differ.

## Workflow

1. Read `./AGENTS.md` first for repo architecture and validation defaults.
2. Confirm Java 21 before running Gradle. On macOS prefer `JAVA_HOME=$(/usr/libexec/java_home -v 21)`.
3. Choose the smallest command that covers the changed modules.
4. Escalate to CI-parity only when module checks pass or when the failure only appears in CI.
5. If a commit reformats files unexpectedly, mention that `.githooks/pre-commit` runs `./gradlew ktlintFormat` on staged Kotlin files.

## Commands

- `core-domain` logic or repository contract changes:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-domain:test`
- `db-core` or `redis` compile checks:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-storage:db-core:compileKotlin`
- `core-api` controller or config compile checks:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-api:compileKotlin`
- CI parity:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-api:build --no-daemon`

## Notes

- Prefer the smallest proof command first.
- Mention JDK mismatch explicitly if the local default Java is newer than 21.
- Read `references/validation.md` when you need the full command matrix or common failure signatures.
