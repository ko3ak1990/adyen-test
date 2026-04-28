plugins {
    id("adyen.jvm-application-conventions")
}

application {
    mainClass.set("com.adyen.pathfilter.PathFilterCliKt")
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

dependencies {
    implementation(libs.snakeyaml)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
}
