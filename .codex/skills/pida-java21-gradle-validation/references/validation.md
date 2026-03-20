# PIDA validation reference

Primary source files:

- `build.gradle.kts`
- `.github/workflows/develop_pull_request.yml`
- `.githooks/pre-commit`

Expected baseline:

- Java: 21
- Build tool: Gradle 8.x
- Main CI command: `:pida-core:core-api:build --no-daemon`

Useful command matrix:

- Controller or DTO changes:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-api:compileKotlin`
- Domain behavior changes:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-domain:test`
- Storage implementation changes:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-storage:db-core:compileKotlin`
- Full CI parity:
  `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-api:build --no-daemon`

Common pitfalls:

- A local JDK newer than 21 can break Gradle or Kotlin script initialization before project code compiles.
- Pre-commit runs `ktlintFormat` and may restage Kotlin files.
- `core-api:build` is the highest-signal validation because it pulls in downstream modules, tests, and lint.
