# MiniRedis

An educational, Redis-inspired in-memory key-value database and TCP server, written in plain Java.

The goal is to understand how a system like Redis works internally by building a simplified but
real implementation: storage engine, command parser, network server, expiration, concurrency,
persistence and the RESP2 protocol.

## Status

Phase 1 complete: in-memory storage engine (set, get, delete, exists) with unit tests. Nothing is runnable over the network yet.

## Requirements

- JDK 21 or newer
- Maven 3.9+

## Build and test

```bash
mvn test
```

## Project layout

```
src/main/java/com/miniredis/   server source code
src/test/java/com/miniredis/   JUnit 5 tests
docs/                          design notes and diagrams
DEVELOPMENT_LOG.md             progress, decisions, next steps
```

## Roadmap

1. In-memory storage engine (SET, GET, DEL, EXISTS)
2. Command processor
3. TCP server with a simple text protocol
4. TTL and key expiration
5. Concurrent clients
6. Lists, sets, sorted sets
7. Persistence
8. RESP2 protocol
9. Separate Spring Boot client application
10. Benchmarking and performance work

## Limitations

This is a learning project. It implements a small subset of Redis commands and does not aim for
Redis compatibility or production use.
