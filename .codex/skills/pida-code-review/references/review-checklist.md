# PIDA code review checklist

Review output shape:

- Put findings first.
- Prefer bug risk, regression risk, missing validation, missing tests, or operational fallout over style comments.
- Include the affected file path and why the behavior is risky.

Module checklist:

- `pida-core:core-api`
  - verify request validation, auth scope, `ApiResponseAdvice`, `ApiExceptionAdvice`, and Swagger metadata
- `pida-core:core-domain`
  - verify facade or service orchestration, transaction boundaries, null handling, and category-specific branching
- `pida-storage:db-core` and `pida-storage:redis`
  - verify repository contracts, query predicates, soft-delete assumptions, cache invalidation, and lock behavior
- `pida-clients:*`
  - verify external contract compatibility, property binding, and retry or fallback assumptions
- `pida-tests:*`
  - verify public API or persistence changes have matching tests, or call out the gap explicitly

Helpful pairings:

- Use `api_reviewer` when controller or response DTO changes dominate.
- Use `db_core_specialist` when repository, cache, or redis changes dominate.
- Use `ci_triager` when the branch already shows a failing Java 21 or Gradle signal.
