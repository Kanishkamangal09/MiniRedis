# MiniRedis Development Log

## Current phase

Phase 0 complete (environment + scaffolding). Phase 1 (storage engine) started.

## Environment (verified 2026-11-19)

- macOS, Apple Silicon (M4)
- JDK 24.0.2 (Homebrew) — compiles with `--release 21`
- Maven: NOT yet installed (install with `brew install maven`)
- VS Code with Extension Pack for Java + GitLens

## Completed

- [x] Project scaffolding: pom.xml, .gitignore, README, docs/, src layout
- [x] Verified javac compiles and runs a Java file

## Pending

- [ ] Install Maven and run `mvn test` successfully
- [ ] Fix git: the home directory is currently a git repository; MiniRedis needs its own repo
- [ ] Phase 1: implement `MemoryStore` (set, get, delete, exists)
- [ ] Phase 1: write `MemoryStoreTest` with JUnit 5

## Concepts learned

- What an in-memory key-value store is and how it differs from MySQL/MongoDB
- Request/response flow from client to database server over TCP
- Map interface, HashMap, generics, encapsulation (Phase 1 lesson)

## Architectural decisions

- Core server in plain Java (no Spring) so that sockets, parsing and threads are visible.
- Maven for builds, JUnit 5 for tests, no other dependencies.
- Package root: `com.miniredis`. Storage engine lives in `com.miniredis.store`.
- Java 21 language level, even though JDK 24 is installed.

## Known bugs

None yet.

## Test status

No tests written yet.

## Next recommended task

Implement `com.miniredis.store.MemoryStore` yourself (see assignment in chat), then write tests.
