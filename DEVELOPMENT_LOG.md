# MiniRedis Development Log

## Current phase

Phase 2 (command processor) implemented and tested. Next: Phase 3 (TCP server).

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
- [x] Phase 2: `Command` record, `CommandParser`, `CommandExecutor`, `CommandException`
- [x] Phase 2: 19 tests for parser and executor (verified 2026-11-26)

## Pending

- [ ] Commit Phase 2 work
- [ ] Phase 3: `RedisServer` with ServerSocket on port 6379, one client at a time

## Concepts learned

- What an in-memory key-value store is and how it differs from MySQL/MongoDB
- Request/response flow from client to database server over TCP
- Map interface, HashMap, generics, encapsulation (Phase 1 lesson)
- Records, static utility classes, String.split with regex, switch expressions, custom exceptions, try/catch, manual dependency injection (Phase 2 lesson)

## Architectural decisions

- Core server in plain Java (no Spring) so that sockets, parsing and threads are visible.
- Maven for builds, JUnit 5 for tests, no other dependencies.
- Package root: `com.miniredis`. Storage engine lives in `com.miniredis.store`.
- Java 21 language level, even though a newer JDK is installed.
- Parsing (`CommandParser`) is separate from execution (`CommandExecutor`) so RESP2 can replace the parser later without touching execution.
- Client errors are a dedicated `CommandException` and become `ERR ...` replies; any other exception is treated as a bug.
- Replies are plain Strings for now. A `Reply` type may be introduced in Phase 8 when RESP2 needs typed replies (integer vs bulk string).

## Known bugs

None yet.

## Test status

34 unit tests across store and command packages, all passing (mvn test, 2026-11-26).

## Next recommended task

Commit, then start Phase 3: a TCP server that reads lines from a socket and feeds them to CommandExecutor.
