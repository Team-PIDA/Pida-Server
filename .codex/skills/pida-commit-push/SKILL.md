---
name: pida-commit-push
description: Use when committing or pushing PIDA changes. Plans safe commit groups, chooses Java 21 validation commands, enforces explicit confirmation, and remembers the repo's pre-commit ktlint behavior.
metadata:
  short-description: Safe PIDA commit and push workflow
---

# PIDA Commit Push

Use this skill when the user asks to commit, push, or do a combined commit-push flow in PIDA.

## Workflow

1. Read `./AGENTS.md` for validation defaults and git workflow notes.
2. Inspect `git status` and split unrelated work into the smallest safe commit groups.
3. For each group, choose the smallest Java 21 validation command that proves the change.
4. Propose the ordered commit and push plan first, using `[Topic] Issue summary` commit messages in English.
5. Wait for explicit approval before mutating git state. Use the exact approval token `확인` when a single-token confirmation is appropriate.
6. After approval, stage one group at a time, commit, re-stage if `.githooks/pre-commit` reformats Kotlin files, then push once at the end.
7. Never revert unrelated user changes unless the user explicitly asks.

## Commit heuristics

- Keep one vertical feature slice in one commit when it builds and tests together.
- Split infra, docs, refactor, and feature work when they are independently understandable.
- Prefer `Feature`, `Fix`, `Refactor`, `Test`, `Docs`, and `Chore` as topic labels.

## Notes

- Prefer `JAVA_HOME=$(/usr/libexec/java_home -v 21)` for Gradle commands.
- Always write PIDA commit messages in English.
- Read `references/commit-push.md` for commit grouping heuristics, validation mapping, and prompt templates.
