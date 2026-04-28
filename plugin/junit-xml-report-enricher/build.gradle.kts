plugins {
    kotlin("jvm") version "2.0.21"
    `java-gradle-plugin`
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    plugins {
        create("junitXmlFilenameConvention") {
            id = "com.adyen.junit-xml-report-enricher"
            implementationClass = "com.adyen.junitxmlfilename.JunitXmlFilenamePlugin"
            displayName = "JUnit XML Report Enricher Plugin"
            description = "Adds filename/line metadata to JUnit XML test reports"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

tasks.test {
    useJUnitPlatform()
}

