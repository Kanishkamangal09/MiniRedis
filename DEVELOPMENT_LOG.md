# MiniRedis Development Log

## Current phase

Phase 1 (storage engine) implemented and tested. Next: Phase 2 (command processor).

## Environment (verified 2026-11-19)

- macOS, Apple Silicon (M4)
- JDK 27 (Homebrew), compiling with `--release 21`
- Maven 3.10.1 installed via Homebrew
- VS Code with Extension Pack for Java + GitLens

## Completed

- [x] Project scaffolding: pom.xml, .gitignore, README, docs/, src layout
- [x] Verified javac compiles and runs a Java file
- [x] Maven installed, `mvn test` works
- [x] MiniRedis is its own git repository
- [x] Phase 1: `MemoryStore` with set/get/delete/exists/size and input validation
- [x] Phase 1: `MemoryStoreTest`, 15 tests (verified 2026-11-26)

## Pending

- [ ] Commit Phase 1 work
- [ ] Phase 2: CommandParser (text line -> tokens) and CommandExecutor

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

15 unit tests in MemoryStoreTest, all passing (mvn test, 2026-11-26).

## Next recommended task

Commit, then start Phase 2: the command processor that turns "SET name alice" into a MemoryStore call.
