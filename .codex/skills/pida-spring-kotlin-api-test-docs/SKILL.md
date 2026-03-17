---
name: pida-spring-kotlin-api-test-docs
description: Use when adding or changing a PIDA API endpoint. Covers controller and DTO patterns, ApiResponseAdvice wrapping, Swagger annotations, and the minimum test and docs checks expected in this repo.
metadata:
  short-description: Review PIDA API tests and docs
---

# PIDA Spring Kotlin API Test Docs

Use this skill for public API additions or contract changes in `pida-core:core-api`.

## Workflow

1. Read `./AGENTS.md` and inspect the nearest existing controller and response DTO in the same API family.
2. Return the internal response payload shape only. Successful responses are wrapped by `ApiResponseAdvice`.
3. Check `@Tag`, `@Operation`, `@Schema`, and example values on request and response DTOs.
4. Decide the smallest missing verification:
   - controller compile only
   - controller/docs test
   - broader integration test if infrastructure is involved
5. Flag missing auth/no-auth coverage and missing documentation updates.

## What to review

- Controller path and annotation consistency
- DTO field descriptions and examples
- `ApiResponseAdvice` and `ApiExceptionAdvice` compatibility
- RestDocs or controller test gaps for new or changed public APIs

## Notes

- Prefer concrete findings about regressions, missing tests, or missing docs.
- Read `references/api-test-docs.md` when you need repo-specific test and doc entrypoints.
