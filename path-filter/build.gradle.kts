plugins {
    id("adyen.jvm-application-conventions")
}

application {
    mainClass.set("com.adyen.pathfilter.PathFilterCliKt")
}

dependencies {
    implementation(libs.snakeyaml)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
}
