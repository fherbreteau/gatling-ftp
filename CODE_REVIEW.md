# Code Review: gatling-ftp

**Date:** 2026-03-09
**Version:** 0.0.10-SNAPSHOT
**Source LOC:** 1,063 | **Test LOC:** 1,115 | **Test suites:** 10 | **Tests:** 77

---

## Summary Scorecard

| Category        | Score    | Notes                                                        |
|-----------------|:--------:|--------------------------------------------------------------|
| Lint Compliance | 5/10     | No linter configured; residual SFTP naming in Scala code     |
| Code Quality    | 5/10     | Silent exception swallowing, discarded results, unsafe casts |
| Security        | 7/10     | Credentials masked in protocol logs; usernames in debug logs |
| Maintainability | 6/10     | Mutable shared state without synchronization; tight coupling |
| Documentation   | 6/10     | JavaDoc fixed; README restructured; inline comments minimal  |
| Idempotency     | 7/10     | Temp file cleanup unreliable; FTP operations non-idempotent  |
| **Overall**     | **6/10** | Good test coverage; silent failure modes and naming debt     |

---

## Repository Structure

```text
gatling-ftp/
├── .github/
│   ├── workflows/
│   │   ├── ci.yml                  # Build + SonarCloud (JDK 17)
│   │   ├── test.yml                # Unit tests (JDK 17)
│   │   ├── release.yml             # Maven Central release via GPG
│   │   ├── dependency-review.yml   # PR dependency scanning
│   │   └── release-notes.yml       # Auto-generated release notes
│   ├── dependabot.yml              # Weekly Maven dependency updates
│   └── release.yml                 # Release drafter config
├── src/main/
│   ├── scala/...gatling/ftp/
│   │   ├── Predef.scala            # Import entrypoint
│   │   ├── Ftp.scala               # Scala DSL (ls, mkdir, upload, etc.)
│   │   ├── FtpDsl.scala            # Protocol + action factory trait
│   │   ├── action/                 # Gatling ActionBuilder / RequestAction
│   │   ├── client/                 # Exchange, FtpOperation, Transaction
│   │   ├── protocol/               # FtpProtocol, Builder, Components
│   │   └── util/                   # FtpHelper (credential expressions)
│   └── java/...gatling/ftp/javaapi/
│       ├── Ftp.java                # Java DSL wrapper
│       ├── FtpDsl.java             # Java entrypoint
│       ├── action/                 # Java ActionBuilder wrapper
│       └── protocol/               # Java ProtocolBuilder wrapper
├── src/test/
│   ├── scala/.../
│   │   ├── FtpDslSpec.scala
│   │   ├── client/FtpOperationBuilderSpec.scala
│   │   ├── client/FtpOperationSpec.scala
│   │   ├── client/FtpTransactionSpec.scala
│   │   ├── client/result/FtpResultSpec.scala
│   │   ├── protocol/FtpProtocolBuilderSpec.scala
│   │   ├── protocol/FtpProtocolSpec.scala
│   │   ├── util/FtpHelperSpec.scala
│   │   ├── integration/FtpIntegrationSpec.scala
│   │   └── examples/               # Gatling simulation examples (2 files)
│   ├── java/.../examples/          # Java simulation examples (1 file)
│   └── resources/                  # Credentials, configs, test data
├── docker-compose.yml              # Alpine FTP test server
├── javadoc/readme.md               # Javadoc overview
├── pom.xml
├── CODE_REVIEW.md
├── LICENSE (MIT)
└── README.md
```

---

## Outstanding Tasks

### Critical

- [ ] **Exchange: discarded `FtpFailure` in `finally` block** -- `Exchange.scala:100-105` constructs a `FtpFailure` when `client.logout()` fails but never assigns or returns it. The result is silently discarded, masking logout failures from stats reporting.

- [ ] **FtpOperation: `Using.Manager` swallows IOExceptions** -- `FtpOperation.scala:54-66,74-81,82-89` wraps Upload, Download, and Copy operations in `Using.Manager` which returns `Try[Unit]`. The `Try` result is discarded, so IOExceptions from failed FTP operations are silently swallowed and never reach the caller. Add `.get` to propagate exceptions.

### High

- [ ] **Unsafe `.get` on credentials** -- `FtpProtocol.scala:68` calls `.toOption.get` which throws `NoSuchElementException` if the credentials expression fails. Consider returning `Validation[Credentials]` or throwing a domain-specific exception.

- [ ] **Unsynchronized mutable state** -- `FtpClientFactory.scala:7` uses a `mutable.Map` without synchronization. Multiple threads submitting transactions concurrently can corrupt the map. Switch to `ConcurrentHashMap` or add synchronization.

- [ ] **Resource leak on disconnect failure** -- `FtpClientFactory.scala:17-21` calls `disconnect()` before removing the entry. If `disconnect()` throws, the entry is never removed from the map and the client leaks.

### Medium

- [ ] **Copy-paste naming: SFTP references in FTP code** -- Rename `ftpProtocolBuilder2SftpProtocol` to `ftpProtocolBuilder2FtpProtocol` in `FtpDsl.scala:13`. Rename `lookUpSftpComponents` to `lookUpFtpComponents` in `FtpActionBuilder.scala:17,32`. Change session key from `"sftp.exchange"` to `"ftp.exchange"` in `FtpClients.scala:7`.

- [x] **JavaDoc copy-paste errors in `Ftp.java`** -- ~~20 occurrences of `@return a new instance of SftpActionBuilder` should say `FtpActionBuilder`.~~ Replaced all occurrences with `FtpActionBuilder`.

- [x] **JavaDoc copy-paste errors in `FtpProtocolBuilder.java`** -- ~~11 occurrences of `@return a new HttpProtocolBuilder instance` should say `FtpProtocolBuilder`. 2 occurrences of `sftp server` on lines 25, 34 should say `FTP server`.~~ Replaced all occurrences with `FtpProtocolBuilder` and `FTP server`.

- [ ] **Unsafe type cast** -- `FtpClients.scala:15` uses `.asInstanceOf[Exchange]` without a type check. Use pattern matching instead: `session.attributes.get(exchange).collect { case e: Exchange => e }`.

- [ ] **Path concatenation without sanitization** -- `FtpProtocol.scala:60,64` builds remote paths via `"".concat("/").concat(file)`. No validation against `../` traversal sequences or double slashes. Add path normalization.

### Low

- [ ] **Typo in error message** -- `Exchange.scala:102` says `"Failed to loggout from server"`. Should be `"logout"`.

- [ ] **Misleading error message** -- `FtpProtocol.scala:21` throws `"Can't provide a default value for ImportProtocol"`. Should reference `FtpProtocol`.

- [ ] **Unreliable temp file cleanup** -- `FtpOperation.scala:67` uses `tempFile.deleteOnExit()` which does not guarantee cleanup on abnormal termination. Replace with explicit deletion in a `try`/`finally` block.

- [ ] **Docker image not pinned** -- `docker-compose.yml` uses `delfer/alpine-ftp-server:latest`. Pin to a specific version for reproducible test environments.

- [x] **Incomplete .gitignore** -- ~~Missing entries for `.DS_Store`, `*.log`, `.env`. Consider adding them.~~ Added `.DS_Store`, `Thumbs.db`, `.env`, `.env.*`, and `*.log` entries.

---

## Security Review

| Area                 | Status | Detail                                                                                      |
|----------------------|--------|---------------------------------------------------------------------------------------------|
| Credential storage   | PASS   | Credentials are session-scoped expressions, not hardcoded                                   |
| Protocol logging     | PASS   | `PrintCommandListener` uses `suppressLogin=true` to mask credentials                        |
| Debug log exposure   | WARN   | `Exchange.scala:81` logs `credentials.username` at DEBUG level                              |
| Credential transport | WARN   | FTP transmits credentials in plaintext (inherent protocol limitation)                       |
| Input validation     | WARN   | No path traversal checks on remote/local file paths                                         |
| Dependency CVEs      | PASS   | No known CVEs in commons-net 3.12.0, Gatling 3.15.0                                         |
| Test credentials     | PASS   | Test users use dummy passwords (`"password"`, `"testpass"`)                                 |
| Temp file exposure   | WARN   | Copy operation creates temp files with predictable prefix `"temp"` in default temp dir      |

---

## Dependency Matrix

| Dependency            | Artifact                      | Version  | Scope   | Status  | Notes                             |
|-----------------------|-------------------------------|----------|---------|---------|-----------------------------------|
| Gatling Core          | `gatling-core-java`           | 3.15.0   | compile | Current | Provides Scala 2.13 transitively  |
| Gatling Charts        | `gatling-charts-highcharts`   | 3.15.0   | test    | Current | Simulation runner + reports       |
| Apache Commons Net    | `commons-net`                 | 3.12.0   | compile | Current | FTP client implementation         |
| Jakarta Annotations   | `jakarta.annotation-api`      | 3.0.0    | compile | Current | `@Nonnull` for Java API           |
| ScalaTest             | `scalatest_2.13`              | 3.2.18   | test    | Current | Unit + integration test framework |
| ScalaTestPlus Mockito | `mockito-5-10_2.13`           | 3.2.18.0 | test    | Current | Mock framework for unit tests     |
| MockFtpServer         | `MockFtpServer`               | 3.2.0    | test    | Current | Embedded FTP server for tests     |

### Build Plugins

| Plugin                    | Version | Phase                                    | Notes                                                        |
|---------------------------|---------|------------------------------------------|--------------------------------------------------------------|
| `scala-maven-plugin`      | 4.9.9   | process-sources / process-test-resources | Compiles Scala + Java sources                                |
| `maven-compiler-plugin`   | 3.15.0  | *(disabled)*                             | Default phases disabled; Scala plugin handles compilation    |
| `gatling-maven-plugin`    | 4.21.0  | manual                                   | Runs Gatling simulations via `mvn gatling:test`              |
| `scalatest-maven-plugin`  | 2.2.0   | test                                     | Runs ScalaTest specs via `mvn -B test`                       |

### Transitive Dependencies (notable)

| Dependency      | Version      | Via                     | Notes                          |
|-----------------|--------------|-------------------------|--------------------------------|
| Scala Library   | 2.13.18      | `gatling-core-java`     | Runtime language support       |
| SLF4J API       | 1.7.36       | `gatling-commons`       | Logging facade                 |
| Logback Classic | 1.5.32       | `gatling-commons`       | Logging implementation         |
| Netty           | 4.2.10.Final | `gatling-core`          | Network I/O (multiple modules) |
| Mockito Core    | 5.10.0       | `scalatestplus-mockito` | Mock framework                 |

---

## Test Verification

| Suite                                        | Type               | Framework                           | Tests  | Status       |
|----------------------------------------------|--------------------|-------------------------------------|--------|--------------|
| Unit tests (`mvn -B test`)                   | Unit + Integration | ScalaTest + Mockito + FakeFtpServer | 77     | PASS         |
| Gatling simulations (`mvn -B gatling:test`)  | E2E (Docker)       | Gatling 3.15.0                      | 2 sims | Not verified |
