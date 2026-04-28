# CI/CD Path Filter + jRich JUnit XML Plugin

This repository contains a Gradle multi-module build with shared convention logic:

- `build-logic` (included build) - reusable Gradle conventions for JVM app, Android app, and Gradle plugin modules
- `:path-filter` - standalone CLI that evaluates changed files against YAML filters and writes a `.env` output
- `jrich-plugin` (included build) - Gradle plugin `com.adyen.jrich` that enriches JUnit XML with `filename` and best-effort `line`
- `:android-app` - Android consumer app module that applies `com.adyen.jrich`


## Prerequisites

- JDK 21+ (required by Android Gradle Plugin 9.x)
- Android SDK installed (for `:android-app` tasks)

## Build and test

```bash
./gradlew build
./gradlew test
./gradlew :android-app:testDebugUnitTest
./gradlew -p jrich-plugin test
```

## Version management

All plugin and dependency versions are centralized in `gradle/libs.versions.toml`.
Included builds (`build-logic` and `jrich-plugin`) import this catalog in their own `settings.gradle.kts`.

## Run the CLI

```bash
./gradlew :path-filter:run --args='--config filters.yaml --output build/result.env app/src/Main.kt docs/README.md'
```

Example `filters.yaml`:

```yaml
filters:
  kotlin_sources:
    - "**/*.kt"
    - "!**/test/**"
  tests:
    - "**/test/**/*.kt"
  documentation:
    - "README.md"
    - "docs/**"
```

The generated `.env` file format is:

```env
kotlin_sources=true
tests=true
documentation=true
```

## Apply jRich plugin in a module

Plugin id:

```text
com.adyen.jrich
```

DSL configuration:

```kotlin
jRich {
    reportDir.set(layout.buildDirectory.dir("reports/jrich"))
}
```

`jRich` writes enriched XML into `reportDir` and leaves original test XML unchanged.

## IntelliJ IDEA module visibility

This project uses a composite build:

- `:path-filter` and `:android-app` are regular subprojects
- `build-logic` and `jrich-plugin` are included builds from `settings.gradle.kts`

If IntelliJ only shows the root project, run **Gradle > Reload All Gradle Projects** and reopen from repository root.

## Verify enrichment on a failing Android test

Run with an opt-in failing test:

```bash
./gradlew :android-app:testDebugUnitTest -PdemoFailTest=true --continue
```

Inspect enriched output (`filename` and best-effort `line`):

```bash
grep -n "filename=\|line=" android-app/build/reports/jrich-demo/testDebugUnitTest/*.xml
```

Original XML should remain unmodified:

```bash
grep -n "filename=\|line=" android-app/build/test-results/testDebugUnitTest/*.xml
```
