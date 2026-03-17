# PIDA API test and docs reference

Primary source files:

- `pida-core/core-api/src/main/kotlin/com/pida/presentation/advice/ApiResponseAdvice.kt`
- `pida-core/core-api/src/main/kotlin/com/pida/presentation/advice/ApiExceptionAdvice.kt`
- `pida-tests/api-docs/src/main/kotlin/com/pida/docs/RestDocsTestSuite.kt`
- `pida-tests/api-docs/src/main/kotlin/com/pida/docs/RestDocsTestUserSuite.kt`
- `pida-supports/swagger`

Repo expectations:

- Controllers return the inner data payload, not a pre-wrapped `ApiResponse`, unless there is a specific exception.
- Public DTOs should keep Swagger annotations and examples in sync with the contract.
- New public endpoints should be reviewed for at least one matching test or a documented reason why a test is deferred.

Review checklist:

- Path and HTTP verb fit the existing API version and family
- Success payload is compatible with `ApiResponseAdvice`
- Error cases remain compatible with `ApiExceptionAdvice`
- Schema descriptions and examples match the actual fields
- Docs/test coverage is called out explicitly when missing
