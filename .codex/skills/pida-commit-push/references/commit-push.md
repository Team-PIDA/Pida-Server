# PIDA commit-push reference

Validation mapping:

- `core-domain` logic or repository contract changes
  - `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-domain:test`
- `db-core` or `redis` compile checks
  - `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-storage:db-core:compileKotlin`
- `core-api` controller or config compile checks
  - `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-api:compileKotlin`
- CI parity before final push when risk is cross-module or user asked for stronger proof
  - `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-api:build --no-daemon`

Commit grouping hints:

- One feature slice across `core-api`, `core-domain`, and `db-core` can stay in one commit if it is independently reviewable.
- Split generated docs, broad formatting churn, or infra-only changes away from product behavior changes.
- If staged Kotlin files are reformatted by the pre-commit hook, restage and re-check before pushing.

Prompt templates:

- Skill-based planning
  - `codex exec -C . 'Use $pida-commit-push to propose a safe commit and push sequence for the current branch. Do not mutate git yet.'`
- Agent-based planning
  - `codex exec -C . "Use commit_push_guard to inspect the current changes, propose safe commit groups, and list the Java 21 checks to run before pushing."`
