# PIDA db-core reference

Primary source files:

- `pida-storage/db-core/src/main/kotlin/com/pida/storage/db/core/flowerspot/FlowerSpotCoreRepository.kt`
- `pida-storage/db-core/src/main/kotlin/com/pida/storage/db/core/support/JpaRepositoryExtensions.kt`
- `pida-core/core-domain/src/main/kotlin/com/pida/support/tx/TransactionTemplates.kt`
- `pida-core/core-domain/src/main/kotlin/com/pida/support/tx/Tx.kt`
- `pida-storage/redis/src/main/kotlin/com/pida/storage/redis/support/CacheCoreRepository.kt`
- `pida-core/core-domain/src/main/kotlin/com/pida/flowerspot/FlowerSpotFinder.kt`

Pattern reminders:

- Put repository interfaces in `core-domain`.
- Put JPA-backed implementations in `db-core` and cache-backed implementations in `redis`.
- Keep `deletedAt` filtering explicit via local query methods or helper extensions.
- Match the transaction helper style already used in the neighboring package instead of mixing styles casually.
- Keep cache key naming and TTL decisions close to finder or service code where the read behavior is defined.
