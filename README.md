# MiniRedis

An educational, Redis-inspired in-memory key-value database and TCP server, written in plain Java.

The goal is to understand how a system like Redis works internally by building a simplified but
real implementation: storage engine, command parser, network server, expiration, concurrency,
persistence and the RESP2 protocol.

## Status

Phase 2 complete: storage engine plus a command processor. Nothing is runnable over the network yet.

## Supported commands

| Command           | Reply                              |
|-------------------|------------------------------------|
| `PING`            | `PONG`                             |
| `SET key value`   | `OK`                               |
| `GET key`         | the value, or `(nil)`              |
| `DEL key`         | `1` if removed, `0` if missing     |
| `EXISTS key`      | `1` or `0`                         |
| `DBSIZE`          | number of keys                     |

Command names are case-insensitive. Errors are replied as `ERR <message>`.

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
