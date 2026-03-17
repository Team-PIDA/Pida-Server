---
name: pida-subagent-usage
description: Use when you want to invoke PIDA project-scoped custom agents from the repository root. Gives dot-based command templates for feature_mapper, ci_triager, api_reviewer, db_core_specialist, code_reviewer, and commit_push_guard.
metadata:
  short-description: Run PIDA custom agents from .
---

# PIDA Subagent Usage

Use this skill when you want ready-to-run command templates for the PIDA project-scoped custom agents.

## Workflow

1. Run commands from the repository root and use `.` as the project path.
2. Pick the smallest agent that matches the task.
3. Use `codex exec -C .` for a one-off check and `codex -C .` for an interactive session.
4. If the work needs more than one focused review, ask Codex to spawn the named agents in parallel.

## Commands

- Open an interactive session:
  `codex -C .`
- Feature mapping:
  `codex exec -C . "Use feature_mapper to map the affected modules, files, and validation scope for this new feature."`
- CI or build triage:
  `codex exec -C . "Use ci_triager to explain why :pida-core:core-api:build is failing under Java 21."`
- API review:
  `codex exec -C . "Use api_reviewer to review this branch for API contract drift, missing tests, and missing docs."`
- Persistence review:
  `codex exec -C . "Use db_core_specialist to review whether this db-core or redis change matches existing repository, transaction, and cache patterns."`
- General code review:
  `codex exec -C . "Use code_reviewer to review this branch for bugs, regressions, and missing tests."`
- Commit and push planning:
  `codex exec -C . "Use commit_push_guard to inspect the current changes, propose safe commit groups, and list the Java 21 checks to run before pushing."`
- Parallel review example:
  `codex exec -C . "Spawn feature_mapper and api_reviewer in parallel for this API change, wait for both, then summarize the result."`

## Notes

- The available project-scoped custom agents are `feature_mapper`, `ci_triager`, `api_reviewer`, `db_core_specialist`, `code_reviewer`, and `commit_push_guard`.
- `commit_push_guard` plans the git workflow but does not mutate git state by itself.
- Read `references/commands.md` when you want a task-to-agent mapping or prompt variants.
