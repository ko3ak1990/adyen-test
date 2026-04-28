plugins {
    alias(libs.plugins.kotlin.jvm)
    id("adyen.gradle-plugin-conventions")
}

gradlePlugin {
    plugins {
        create("jRich") {
            id = "com.adyen.jrich"
            implementationClass = "com.adyen.junitxmlfilename.JunitXmlFilenamePlugin"
            displayName = "jRich"
            description = "Adds filename/line metadata to JUnit XML test reports"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
    testImplementation(libs.junit.jupiter)
}
