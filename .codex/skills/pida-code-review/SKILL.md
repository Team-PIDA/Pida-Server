---
name: pida-code-review
description: Use when reviewing a PIDA branch, diff, or PR. Focus on bugs, regressions, missing tests, API contract drift, persistence risks, and operational issues before style comments.
metadata:
  short-description: Review PIDA changes for real risks
---

# PIDA Code Review

Use this skill when the user asks for a review, PR review, regression scan, or pre-merge check in PIDA.

## Workflow

1. Read `./AGENTS.md` for module boundaries and validation defaults.
2. Map changed modules first, then review behavior risks before style.
3. Prioritize findings about API contracts, auth, transactions, cache, schedulers, external clients, and missing tests or docs.
4. Output findings first, ordered by severity, with file references and a short explanation of impact.
5. If no findings exist, say so explicitly and mention residual test or verification risk.

## Focus areas

- `core-api`: endpoint path and verb, auth, request validation, `ApiResponseAdvice`, `ApiExceptionAdvice`, Swagger annotations
- `core-domain`: facade or service boundaries, null handling, transaction assumptions, branching logic
- `db-core` and `redis`: query filters, soft delete, repository contracts, cache invalidation, lock semantics
- `clients`: request or response DTO compatibility, configuration properties, retry or error handling
- tests and docs: matching RestDocs or tests for public API or persistence behavior changes

## Notes

- Prefer concrete regressions and missing coverage over style comments.
- Read `references/review-checklist.md` when you need the module-by-module checklist or review output shape.
