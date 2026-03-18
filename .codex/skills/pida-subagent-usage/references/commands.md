# PIDA subagent command reference

Use these commands from the repository root.

Interactive session:

- `codex -C .`

One-off exec patterns:

- feature mapping
  - `codex exec -C . "Spawn feature_mapper to map the affected modules, files, and validation scope for this feature. Wait for it and return one implementation plan."`
- CI and build triage
  - `codex exec -C . "Spawn ci_triager to explain why :pida-core:core-api:build is failing under Java 21. Return the root cause, the smallest proof command, and the next fix to try."`
- API review
  - `codex exec -C . "Spawn api_reviewer to review this branch against main for API contract drift, auth behavior, missing tests, and missing docs. Wait for it and summarize the findings."`
- persistence review
  - `codex exec -C . "Spawn db_core_specialist to review whether this db-core or redis change matches existing repository, transaction, cache, and soft-delete patterns. Wait for it and summarize the real risks."`
- general code review
  - `codex exec -C . "Spawn code_reviewer to review this branch against main for bugs, regressions, and missing tests. Wait for it and summarize the concrete findings."`
- commit and push planning
  - `codex exec -C . "Spawn commit_push_guard to inspect the current changes, propose safe commit groups, and list the Java 21 checks to run before pushing. Do not mutate git."`

Parallel prompt template:

- `codex exec -C . "Review this branch against main. Spawn api_reviewer, db_core_specialist, and code_reviewer in parallel. Have api_reviewer focus on contract drift, docs, and tests. Have db_core_specialist focus on repository, transaction, cache, and soft-delete consistency. Have code_reviewer focus on bugs and regressions. Wait for all of them and summarize only concrete findings."`

Task-to-agent mapping:

- new feature or API slice: `feature_mapper`
- Gradle, Java 21, ktlint, CI failures: `ci_triager`
- controller or DTO contract gaps: `api_reviewer`
- db-core, redis, repository, cache consistency: `db_core_specialist`
- branch-wide regression review: `code_reviewer`
- commit grouping and push readiness: `commit_push_guard`

Quick reminders:

- `/agent` only shows the currently active threads.
- If you do not say `Spawn`, Codex may answer in the main thread without creating a child agent.
- Prefer one writer and multiple read-only reviewers.
