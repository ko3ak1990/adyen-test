import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    `java-gradle-plugin`
}

plugins.withId("org.jetbrains.kotlin.jvm") {
    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(17)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

