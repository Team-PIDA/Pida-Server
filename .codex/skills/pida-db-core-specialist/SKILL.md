---
name: pida-db-core-specialist
description: Use when changing PIDA persistence or cache layers. Covers Entity, JpaRepository, CoreRepository, transaction helpers, soft delete filters, Redis cache placement, and geospatial patterns.
metadata:
  short-description: Review PIDA db-core patterns
---

# PIDA DB Core Specialist

Use this skill when a change touches `pida-storage`, repository interfaces, or cache-heavy finder paths.

## Workflow

1. Read `./AGENTS.md` and inspect the nearest neighboring package before proposing a new storage pattern.
2. Keep the vertical slice aligned:
   - repository interface in `core-domain`
   - implementation in `db-core` or `redis`
   - finder or service usage in `core-domain`
3. Preserve soft-delete behavior and transaction helper consistency with the surrounding package.
4. Keep cache keys and TTL decisions in finder or service layers, not in entities.
5. Call out when a change should mirror an existing package instead of inventing a new abstraction.

## Focus areas

- `Entity`, `JpaRepository`, `CoreRepository`
- `deletedAt` filtering and repository extension helpers
- `TransactionTemplates` or local transaction helper usage
- cache key placement and TTL ownership
- GeoJson and PostGIS related patterns

## Notes

- Prefer the pattern used by the nearest peer package over repo-wide refactors.
- Read `references/patterns.md` when you need concrete source examples.
