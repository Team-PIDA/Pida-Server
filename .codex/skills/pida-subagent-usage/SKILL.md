---
name: pida-subagent-usage
description: Use when you want to invoke PIDA project-scoped custom agents from the repository root. Gives ready-to-run Spawn-based command templates and prompt patterns for feature_mapper, ci_triager, api_reviewer, db_core_specialist, code_reviewer, and commit_push_guard, with Korean output by default.
metadata:
  short-description: Run PIDA custom agents with Korean Spawn prompts
---

# PIDA Subagent Usage

Use this skill when you want ready-to-run command templates or prompt patterns for the PIDA project-scoped custom agents.

## Workflow

1. Run commands from the repository root and use `.` as the project path.
2. Prefer explicit `Spawn ...` phrasing so Codex actually creates child agents.
3. Pick the smallest agent that matches the task, and only fan out when the work is truly parallelizable.
4. Use `codex exec -C .` for a one-off check and `codex -C .` for an interactive session.
5. When using more than one agent, tell Codex what each one should focus on, ask it to wait for all of them, and request one final summary.
6. Default every user-facing response, progress update, finding, and final summary to Korean unless another language is explicitly requested.

## Commands

- Open an interactive session:
  `codex -C .`
- Feature mapping in an interactive session:
  `Spawn feature_mapper to map the affected modules, files, and validation scope for this feature. Wait for it and return one implementation plan in Korean.`
- Feature mapping:
  `codex exec -C . "Spawn feature_mapper to map the affected modules, files, and validation scope for this feature. Wait for it and return one implementation plan in Korean."`
- CI or build triage:
  `codex exec -C . "Spawn ci_triager to explain why :pida-core:core-api:build is failing under Java 21. Return the root cause, the smallest proof command, and the next fix to try in Korean."`
- API review:
  `codex exec -C . "Spawn api_reviewer to review this branch against main for API contract drift, auth behavior, missing tests, and missing docs. Wait for it and summarize the findings in Korean."`
- Persistence review:
  `codex exec -C . "Spawn db_core_specialist to review whether this db-core or redis change matches existing repository, transaction, cache, and soft-delete patterns. Wait for it and summarize the real risks in Korean."`
- General code review:
  `codex exec -C . "Spawn code_reviewer to review this branch against main for bugs, regressions, and missing tests. Wait for it and summarize the concrete findings in Korean."`
- Commit and push planning:
  `codex exec -C . "Spawn commit_push_guard to inspect the current changes, propose safe commit groups, and list the Java 21 checks to run before pushing. Do not mutate git. Respond in Korean."`
- Parallel review example:
  `codex exec -C . "Review this branch against main. Spawn api_reviewer, db_core_specialist, and code_reviewer in parallel. Have api_reviewer focus on contract drift, docs, and tests. Have db_core_specialist focus on repository, transaction, cache, and soft-delete consistency. Have code_reviewer focus on bugs and regressions. Wait for all of them and summarize only concrete findings in Korean."`

## Notes

- The available project-scoped custom agents are `feature_mapper`, `ci_triager`, `api_reviewer`, `db_core_specialist`, `code_reviewer`, and `commit_push_guard`.
- `/agent` only shows active agent threads in the current session. It does not list every available custom agent.
- Codex only creates child agents when you explicitly ask it to spawn them.
- `commit_push_guard` plans the git workflow but does not mutate git state by itself.
- Prefer read-only review and analysis agents first, then use one main writer if code changes are needed.
- Do not use subagents for trivial single-file edits.
- Read `references/commands.md` when you want a task-to-agent mapping.
- Read `references/prompt-library.md` when you want copy-paste prompt variants for common PIDA workflows.
