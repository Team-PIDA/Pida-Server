# PIDA Codex Guide

## Architecture

- `pida-core:core-api`: controllers, request and response DTOs, security, schedulers, application wiring
- `pida-core:core-domain`: business logic and orchestration using `Facade`, `Service`, `Finder`, `Appender`, and repository interfaces
- `pida-storage:db-core`: JPA entities, repositories, and storage implementations
- `pida-storage:redis`: Redis-backed repositories and cache infrastructure
- `pida-clients:*`: external API and infrastructure clients
- `pida-tests:*`: RestDocs, test containers, and fixture helpers

## Validation defaults

- Use Java 21 for every Gradle command.
- On macOS prefer:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21)`
- High-signal CI parity command:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-api:build --no-daemon`
- Smaller checks:
  - `:pida-core:core-domain:test`
  - `:pida-storage:db-core:compileKotlin`
  - `:pida-core:core-api:compileKotlin`
- `.githooks/pre-commit` runs `./gradlew ktlintFormat` on staged Kotlin files.

## Auto-loading

- Run `codex -C .` from the repository root.
- Codex automatically loads project-scoped skills from `.codex/skills/` and custom agents from `.codex/agents/`.
- New developers do not need a separate install step for these repo-local skills and agents.
- Personal global skills can coexist, but repo-local guidance should be the default for PIDA work.
- The `/agent` view only shows active agent threads in the current session. It does not list every available custom agent.

## Custom agents

Use project-scoped custom agents from `.codex/agents/` only when the work is parallelizable or needs focused review.

- `feature_mapper`
  - Use when starting a new API or feature and you need the affected modules and entrypoints mapped first.
- `ci_triager`
  - Use when Gradle, Java 21, ktlint, pre-commit, or GitHub Actions behavior needs root-cause analysis.
- `api_reviewer`
  - Use when a controller or DTO changed and you want contract, test, or docs gaps reviewed.
- `db_core_specialist`
  - Use when a change touches `db-core`, `redis`, repository interfaces, soft delete, or cache patterns.
- `code_reviewer`
  - Use when you want a branch-wide review that prioritizes bugs, regressions, and missing tests.
- `commit_push_guard`
  - Use when you want commit grouping, Java 21 validation, and push-readiness planning before mutating git history.

## Repo-local skills

- `pida-code-review`
  - Branch and PR review guidance tuned for PIDA architecture and test expectations.
- `pida-commit-push`
  - Repo-specific commit and push workflow with Java 21 validation and explicit confirmation.
- `pida-subagent-usage`
  - Dot-based command templates for the project-scoped custom agents.

## Quick commands

Run these from the repository root and use `.` as the current project path.

- Start an interactive Codex session for this repo:
  `codex -C .`
- Ask `feature_mapper` to map a new feature:
  `codex exec -C . "Spawn feature_mapper to map the affected modules, files, and validation scope for this feature. Wait for it and return one implementation plan in Korean."`
- Ask `ci_triager` to analyze a build or CI failure:
  `codex exec -C . "Spawn ci_triager to explain why :pida-core:core-api:build is failing under Java 21. Return the root cause, the smallest proof command, and the next fix to try in Korean."`
- Ask `api_reviewer` to review an API change:
  `codex exec -C . "Spawn api_reviewer to review this branch against main for API contract drift, auth behavior, missing tests, and missing docs. Wait for it and summarize the findings in Korean."`
- Ask `db_core_specialist` to review persistence changes:
  `codex exec -C . "Spawn db_core_specialist to review whether this db-core or redis change matches existing repository, transaction, cache, and soft-delete patterns. Wait for it and summarize the real risks in Korean."`
- Ask `code_reviewer` to review a branch:
  `codex exec -C . "Spawn code_reviewer to review this branch against main for bugs, regressions, and missing tests. Wait for it and summarize the concrete findings in Korean."`
- Ask `commit_push_guard` to plan commit and push steps:
  `codex exec -C . "Spawn commit_push_guard to inspect the current changes, propose safe commit groups, and list the Java 21 checks to run before pushing. Do not mutate git. Respond in Korean."`
- Run a parallel review across multiple agents:
  `codex exec -C . "Review this branch against main. Spawn api_reviewer, db_core_specialist, and code_reviewer in parallel. Have api_reviewer focus on contract drift, docs, and tests. Have db_core_specialist focus on repository, transaction, cache, and soft-delete consistency. Have code_reviewer focus on bugs and regressions. Wait for all of them and summarize only concrete findings in Korean."`
- Use the repo-local code review skill directly:
  `codex exec -C . 'Use $pida-code-review to review this branch for bugs, regressions, and missing tests.'`
- Use the repo-local commit/push skill directly:
  `codex exec -C . 'Use $pida-commit-push to propose a safe commit and push sequence for the current branch. Do not mutate git yet.'`

## Usage rules

- Do not use subagents for trivial single-file edits.
- Prefer read-only custom agents for exploration, review, and failure analysis.
- `commit_push_guard` plans only. Actual `git add`, `git commit`, and `git push` still require explicit confirmation on the main thread.
- Keep vertical slices aligned across `core-api`, `core-domain`, `db-core`, `redis`, and `clients`.
- For persistence changes, mirror the nearest neighboring package before inventing a new pattern.
- For Kotlin public DTO and domain model files, prefer one top-level public class per file. Split wrapper, item, detail, and helper models by concern instead of bundling sibling `data class` declarations into one file.
