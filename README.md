# CI/CD Path Filter + JUnit XML Filename Convention Plugin

This repository contains a Gradle project implementing the coding assignment:

- `:cli-path-filter` - standalone CLI to evaluate changed files against YAML-based filters and emit a `.env` file
- `plugin-junit-xml-report-enricher` (included build) - Gradle convention plugin that enriches JUnit XML with `filename` and best-effort `line` attributes
- `:demo-android-app` - Android app module with a simple `TestActivity` UI and plugin integration

## Prerequisites

- JDK 17+
- Android SDK installed (for `:demo-android-app` tasks)

## Build and test

```bash
./gradlew build
./gradlew test
./gradlew :demo-android-app:testDebugUnitTest
```

## Run the CLI

```bash
./gradlew :cli-path-filter:run --args='--config filters.yaml --output build/result.env app/src/Main.kt docs/README.md'
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

The generated `.env` file uses `name=true|false` lines, for example:

```env
kotlin_sources=true
tests=true
documentation=true
```

## Apply plugin in a module

The plugin id is:

```text
com.adyen.junit-xml-report-enricher
```

Configuration block:

```kotlin
junitXmlFilename {
    outputDir.set(layout.buildDirectory.dir("reports/junit-enriched"))
}
```

After test tasks finish, enriched XML reports are written to the configured output directory while original JUnit XML files remain unchanged.

The plugin uses dedicated Gradle enrichment tasks (instead of `doLast` hooks) for better configuration-cache behavior.

## Demo app activity

Run `:demo-android-app` to see a small activity with green "Adyen" text and a button that cycles the text color through multiple green shades.

## IntelliJ IDEA module visibility

This project uses a composite build:

- `:cli-path-filter` and `:demo-android-app` are regular subprojects
- `plugin-junit-xml-report-enricher` is an included build from `settings.gradle.kts`

If IntelliJ only shows the root project, use **Gradle > Reload All Gradle Projects** and reopen the project from the repository root.

## Verify enrichment on a failing Android test

Run the demo tests with an opt-in failing test:

```bash
./gradlew :demo-android-app:testDebugUnitTest -PdemoFailTest=true --continue
```

Then inspect enriched output (should contain `filename` and best-effort `line` for the failing testcase):

```bash
grep -n "filename=\|line=" demo/android-app/build/reports/junit-enriched-demo/testDebugUnitTest/*.xml
```

Original test XML should stay unmodified:

```bash
grep -n "filename=\|line=" demo/android-app/build/test-results/testDebugUnitTest/*.xml
```

