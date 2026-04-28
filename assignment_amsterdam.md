# Coding Assignment

# Part 1: CI/CD Filter System

## Problem Statement

Build a command-line tool that works like an analogue of GitHub Actions path-based filter tools, such as [paths-changes-filter](https://github.com/marketplace/actions/paths-changes-filter).
The goal is to optimize CI/CD by deciding which pipelines or jobs should run based on the files changed in a commit or merge request.

The tool should read a YAML file that defines named filters as glob patterns, 
compare those filters against a list of changed file paths, 
and output which filters matched in a simple environment-file format that GitLab CI can consume. 
This makes it possible to trigger only the relevant backend, frontend, docs, or test pipelines instead of running everything on every change.

## Requirements

### Submission Requirements

- Publish your solution to a public Git repository (GitHub, GitLab, etc.)
- Implement the project as a Gradle-based application
- Include a README with instructions on how to build and run the tool

### Functional Requirements

#### 1. YAML Configuration Parsing
- Parse a `filters.yaml` file with the following structure:
  ```yaml
  filters:
    backend:
      - "app/src/**/*.kt"
      - "!app/src/test/**"
    frontend:
      - "web/**/*.ts"
      - "web/**/*.tsx"
    docs:
      - "README.md"
      - "docs/**/*.md"
  ```

#### 2. Glob Pattern Matching
- Support standard glob patterns:
  - `*` matches any characters within a path segment
  - `**` matches zero or more path segments
- Support **exclusion patterns** prefixed with `!`
  - Example: `"**/*.kt"` includes all Kotlin files, `"!**/test/**"` excludes test files

#### 3. Filter Evaluation Logic
A filter is considered **matched** (true) if:
- **ANY** changed file matches at least one inclusion pattern
- **AND** that file does NOT match any exclusion pattern

**Inputs:**
- path to the YAML configuration file
- one or more changed file paths

**Outputs:**
- output `.env` file


#### 5. Output Format
Generate an environment file with filter results:
```
tests=true
backend=false
frontend=true
```

## Example Use Case

**filters.yaml:**
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

**Expected Output (.env):**
```
kotlin_sources=true
tests=true
documentation=true
```

**Explanation:**
- `kotlin_sources`: Matches `App.kt` (Kotlin file, not in test directory)
- `tests`: Matches `FiltersTest.kt` (in test directory)
- `documentation`: Matches `README.md`

# Part 2: XML Filename Attribute Plugin

## Problem Statement

Build a Gradle plugin that post-processes JUnit XML test results to add a `filename` attribute to each test case. This attribute should contain the relative path to the source file where the test is defined, making it easier to navigate from test results to source code in CI/CD reporting tools.

## Requirements

- The plugin should automatically hook into Gradle's `Test` tasks
- For each test case in the JUnit XML output, add a `filename` attribute containing the relative path from the project root to the source file
- Separate Output Directory: Modified XML files must be written to a separate output directory (not overwriting the original JUnit XML files). The output location should be configurable via an extension

## Bonus Task

For extra credit, enhance the plugin to include a `line` attribute that indicates the line number where a test failure or assertion occurred.

## Example

Given a JUnit XML file with:
```xml
<testcase name="testExample" classname="com.adyen.filters.FiltersTest" time="0.123"/>
```

The plugin should produce:
```xml
<testcase name="testExample" classname="com.adyen.filters.FiltersTest" time="0.123" filename="filters/src/test/kotlin/com/adyen/filters/FiltersTest.kt"/>
```
