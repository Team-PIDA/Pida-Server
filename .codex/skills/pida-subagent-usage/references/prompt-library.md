# PIDA subagent prompt library

Use these prompts as copy-paste starters from the repository root.

## Interactive startup

- `codex -C .`

## Single-agent prompts

- feature scope before implementation
  - `Spawn feature_mapper to map the affected modules, files, and validation scope for this feature. Wait for it and return one implementation plan.`

- branch review
  - `Spawn code_reviewer to review this branch against main for bugs, regressions, and missing tests. Wait for it and summarize the concrete findings.`

- API contract review
  - `Spawn api_reviewer to review this branch against main for API contract drift, auth behavior, missing tests, and missing docs. Wait for it and summarize the findings.`

- persistence review
  - `Spawn db_core_specialist to review whether this db-core or redis change matches existing repository, transaction, cache, and soft-delete patterns. Wait for it and summarize the real risks.`

- CI triage
  - `Spawn ci_triager to explain why :pida-core:core-api:build is failing under Java 21. Return the root cause, the smallest proof command, and the next fix to try.`

- commit planning
  - `Spawn commit_push_guard to inspect the current changes, propose safe commit groups, and list the Java 21 checks to run before pushing. Do not mutate git.`

## Parallel prompts

- feature planning plus API risk check
  - `Spawn feature_mapper and api_reviewer in parallel. Have feature_mapper map the affected modules, files, and validation scope for this feature. Have api_reviewer list API contract, auth, docs, and test risks if controllers or DTOs change. Wait for both and return one implementation plan.`

- branch-wide review across multiple angles
  - `Review this branch against main. Spawn api_reviewer, db_core_specialist, and code_reviewer in parallel. Have api_reviewer focus on contract drift, docs, and tests. Have db_core_specialist focus on repository, transaction, cache, and soft-delete consistency. Have code_reviewer focus on bugs and regressions. Wait for all of them and summarize only concrete findings.`

- DB-heavy feature review
  - `Spawn feature_mapper and db_core_specialist in parallel. Have feature_mapper map the modules, repository interfaces, and validation scope touched by this feature. Have db_core_specialist review repository, cache, and soft-delete consistency risks. Wait for both and return one implementation plan plus the main persistence risks.`

- pre-merge validation planning
  - `Spawn code_reviewer and commit_push_guard in parallel. Have code_reviewer review this branch against main for bugs, regressions, and missing tests. Have commit_push_guard propose safe commit groups and the smallest Java 21 validation commands for each group. Wait for both and summarize findings first, then the commit plan.`

## Usage rules

- Use subagents when the work is parallelizable or when intermediate exploration would pollute the main context.
- Do not use subagents for trivial single-file edits.
- Prefer read-only reviewers first, then one main writer if implementation is needed.
- `/agent` shows only active agent threads, not every available custom agent.
