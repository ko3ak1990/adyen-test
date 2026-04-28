plugins {
    id("adyen.android-application-conventions")
    id("com.adyen.jrich")
}

android {
    namespace = "com.adyen.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adyen.demo"
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests.all {
            it.systemProperty("demo.failTest", providers.gradleProperty("demoFailTest").orElse("false").get())
        }
    }
}

jRich {
    reportDir.set(layout.buildDirectory.dir("reports/jrich-demo"))
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit4)
}
