# CI/CD Path Filter + jRich

This repository contains two complementary CI/CD tools built as a Gradle multi-module project. Together they let you run only the relevant jobs on a PR and get navigable test failure reports back out.

## How the pieces fit together

```
PR opened / commit pushed
        │
        ▼
  git diff --name-only          ← list of changed files
        │
        ▼
  path-filter CLI
  ┌──────────────────────────────────────┐
  │  input:  filters.yaml                │
  │          changed file paths          │
  │  output: result.env                  │
  │          kotlin_sources=true         │
  │          tests=true                  │
  │          documentation=false         │
  └──────────────────────────────────────┘
        │
        ▼
  CI pipeline reads result.env
  ┌──────────────────────────────────────────────────────┐
  │  if kotlin_sources=true  →  run compile + lint job   │
  │  if tests=true           →  run test job             │
  │  if documentation=false  →  skip doc pipeline        │
  └──────────────────────────────────────────────────────┘
        │
        ▼  (test job runs)
  Gradle executes :test
  ┌──────────────────────────────────────┐
  │  produces test-results/*.xml         │
  │  (no filename or line attributes)    │
  └──────────────────────────────────────┘
        │
        ▼  (jRich hooks in automatically as a finalizer)
  jRich plugin enriches XML
  ┌──────────────────────────────────────┐
  │  reads    test-results/*.xml         │
  │  resolves classname → source file    │
  │  extracts line from stack trace      │
  │  writes   reports/jrich/*.xml        │
  └──────────────────────────────────────┘
        │
        ▼
  CI test reporter / IDE
  (test failures now link directly to source files)
```

---

## Repository layout

| Module | Type | Description |
|---|---|---|
| `build-logic` | included build | Shared Gradle convention plugins for JVM apps, Android apps, and Gradle plugin modules |
| `:path-filter` | JVM CLI application | Evaluates changed file paths against YAML-defined glob filters, writes a `.env` result |
| `jrich-plugin` | included build / Gradle plugin | `com.adyen.jrich` — enriches JUnit XML test reports with `filename` and `line` attributes |
| `:android-app` | Android application | Demo consumer that applies `com.adyen.jrich` |

---

## Prerequisites

- JDK 21+ (required by Android Gradle Plugin 9.x)
- Android SDK (for `:android-app` tasks only)

---

## path-filter

### What it does

`path-filter` reads a `filters.yaml` file and a list of changed file paths, evaluates each named filter against the changed files using glob patterns, and writes a `key=true/false` environment file that CI pipelines can consume to skip irrelevant jobs.

### How it works

```
YamlConfigParser
  └─ parses filters.yaml into FiltersFile
       (map of filter name → pattern list)

FilterEvaluator
  └─ for each filter:
       split patterns into includes / excludes (prefix "!")
       changedFiles.any { file →
         includes.any  { pattern → GlobMatcher.matches(pattern, file) }
         excludes.none { pattern → GlobMatcher.matches(pattern, file) }
       }

GlobMatcher
  └─ wraps java.nio.file.PathMatcher (glob)
     PathMatcher objects are compiled once and cached per pattern
     For patterns containing "/**/" a collapsed fallback is tried
     to handle the zero-segment case java.nio treats strictly

EnvWriter
  └─ writes name=true / name=false lines to the output file
```

### Filter evaluation logic

A filter is **matched** (`true`) when at least one changed file satisfies **all** inclusion patterns **and** is not excluded by any exclusion pattern (prefixed `!`).

```yaml
filters:
  kotlin_sources:
    - "**/*.kt"        # include all Kotlin files
    - "!**/test/**"    # except those under a test directory
  tests:
    - "**/test/**/*.kt"
  documentation:
    - "README.md"
    - "docs/**"
```

### Build and run

```bash
./gradlew :path-filter:run --args='--config filters.yaml --output build/result.env file1.kt src/test/Foo.kt docs/guide.md'
```

Output `build/result.env`:

```env
kotlin_sources=true
tests=true
documentation=true
```

### CLI reference

```
path-filter --config <filters.yaml> --output <result.env> [changed-file ...]
```

| Argument | Required | Description |
|---|---|---|
| `--config` | yes | Path to the YAML filter definition file |
| `--output` | yes | Path where the `.env` result file is written |
| `[files...]` | | Changed file paths (typically from `git diff --name-only`) |

---

## jRich plugin

### What it does

`jRich` is a Gradle plugin (`com.adyen.jrich`) that automatically post-processes JUnit XML test results after every `Test` task. It adds two attributes to each `<testcase>` element:

- **`filename`** — relative path from the project root to the source file (e.g. `app/src/main/kotlin/com/example/Foo.kt`)
- **`line`** — best-effort line number extracted from the first matching frame in the failure stack trace

Original `test-results/` files are never modified. Enriched XML is written to a separate configurable output directory.

### How it works

```
JRichPlugin.apply(project)
  ├─ registers extension "jRich" (JRichExtension)
  │    reportDir  →  default: build/reports/jrich
  │
  └─ registers task "enrichJunitXmlReports" (EnrichJunitXmlReportTask)
       finalizes all Test tasks in the project
       │
       ▼
  EnrichJunitXmlReportTask.enrich()
       │
       ▼
  JunitXmlTaskProcessor.processTask(projectDir, reportDir, outputRoot)
    ├─ SourceFileResolver.discoverSourceRoots(projectDir)
    │    walks src/ looking for kotlin/ and java/ directories
    │
    ├─ for each *.xml in reportDir:
    │
    └─ JunitXmlEnricher.enrichFile(input, output)
         ├─ parses XML with DocumentBuilderFactory
         ├─ for each <testcase classname="…">:
         │    ├─ SourceFileResolver.resolve(classname)
         │    │    strips inner class suffix, converts dots → path segments
         │    │    tries .kt and .java extensions across all source roots
         │    │    returns project-relative path or null
         │    │
         │    └─ StackTraceLineExtractor.extract(stackTrace, filename)
         │         scans failure text for a frame matching the filename
         │         extracts the line number from (FileName.kt:42) notation
         │
         └─ writes enriched XML to output path (preserving relative structure)
```

### Apply the plugin

In `settings.gradle.kts` of the consuming project, make the plugin available:

```kotlin
pluginManagement {
    includeBuild("jrich-plugin")
}
```

In the module's `build.gradle.kts`:

```kotlin
plugins {
    id("com.adyen.jrich")
}
```

### DSL configuration

```kotlin
jRich {
    reportDir.set(layout.buildDirectory.dir("reports/my-enriched-xml"))
}
```

| Property | Default | Description |
|---|---|---|
| `reportDir` | `build/reports/jrich` | Directory where enriched XML files are written |

The task name is `enrichJunitXmlReports` and it belongs to the `verification` group. It runs automatically after every `Test` task in the project — no manual wiring needed.

### Verify enrichment

Run with a forced failure to see the enriched output in action:

```bash
./gradlew :android-app:testDebugUnitTest -PdemoFailTest=true --continue
```

Inspect the enriched XML (`filename` and `line` attributes should be present):

```bash
grep -n 'filename=\|line=' android-app/build/reports/jrich-demo/testDebugUnitTest/*.xml
```

Confirm the original XML is untouched:

```bash
grep -n 'filename=\|line=' android-app/build/test-results/testDebugUnitTest/*.xml
```

---

## Build and test

```bash
# build and test everything
./gradlew build

# test path-filter only
./gradlew :path-filter:test

# test jRich plugin only
./gradlew -p jrich-plugin test

# run Android unit tests
./gradlew :android-app:testDebugUnitTest
```

## Version management

All dependency and plugin versions are centralised in `gradle/libs.versions.toml`. Both included builds (`build-logic` and `jrich-plugin`) import this catalog via their own `settings.gradle.kts`.

## IntelliJ IDEA

This project uses a composite build. If IntelliJ only shows the root project after cloning, run **Gradle → Reload All Gradle Projects** and reopen from the repository root.
