pluginManagement {
    includeBuild("plugin/junit-xml-report-enricher") {
        name = "plugin-junit-xml-report-enricher"
    }
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "adyen-test"

include(":cli-path-filter")
project(":cli-path-filter").projectDir = file("cli/path-filter")

include(":demo-android-app")
project(":demo-android-app").projectDir = file("demo/android-app")
