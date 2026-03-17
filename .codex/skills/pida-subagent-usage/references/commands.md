# PIDA subagent command reference

Use these commands from the repository root.

Interactive session:

- `codex -C .`

One-off exec patterns:

- feature mapping
  - `codex exec -C . "Use feature_mapper to map the affected modules, files, and validation scope for this new feature."`
- CI and build triage
  - `codex exec -C . "Use ci_triager to explain why :pida-core:core-api:build is failing under Java 21."`
- API review
  - `codex exec -C . "Use api_reviewer to review this branch for API contract drift, missing tests, and missing docs."`
- persistence review
  - `codex exec -C . "Use db_core_specialist to review whether this db-core or redis change matches existing repository, transaction, and cache patterns."`
- general code review
  - `codex exec -C . "Use code_reviewer to review this branch for bugs, regressions, and missing tests."`
- commit and push planning
  - `codex exec -C . "Use commit_push_guard to inspect the current changes, propose safe commit groups, and list the Java 21 checks to run before pushing."`

Parallel prompt template:

- `codex exec -C . "Spawn feature_mapper and api_reviewer in parallel for this API change, wait for both, then summarize the result."`

Task-to-agent mapping:

- new feature or API slice: `feature_mapper`
- Gradle, Java 21, ktlint, CI failures: `ci_triager`
- controller or DTO contract gaps: `api_reviewer`
- db-core, redis, repository, cache consistency: `db_core_specialist`
- branch-wide regression review: `code_reviewer`
- commit grouping and push readiness: `commit_push_guard`
