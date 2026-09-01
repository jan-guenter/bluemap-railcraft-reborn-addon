# Changelog

## 0.1.0-alpha.2 - 2026-09-01

- Migrated the accepted renderer to the exact BlueMap 5.23 feature backport.
- Replaced local runtime, registry, resource-extension, and synthetic-dispatch
  helpers with the pinned shared Adapter API source module.
- Kept the alpha.1 gallery, profile, void-chest mesh, and stock fallback
  behavior unchanged.

## 0.1.0-alpha.1 - 2026-08-26

- Generated a fail-closed Java 21 BlueMap add-on seed for `railcraft-1.2.10`.
- Added exact-gated closed void-chest rendering for all horizontal facings and
  waterlogged states using only the operator-installed Railcraft texture.
