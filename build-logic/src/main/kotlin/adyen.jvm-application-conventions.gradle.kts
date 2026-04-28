plugins {
    kotlin("jvm")
    application
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

