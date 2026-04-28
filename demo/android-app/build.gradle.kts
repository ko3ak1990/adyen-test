plugins {
    id("com.android.application")
    kotlin("android")
    id("com.adyen.junit-xml-report-enricher")
}

android {
    namespace = "com.adyen.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adyen.demo"
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            it.reports.junitXml.required.set(true)
            it.systemProperty("demo.failTest", providers.gradleProperty("demoFailTest").orElse("false").get())
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    testImplementation("junit:junit:4.13.2")
}

extensions.configure<com.adyen.junitxmlfilename.JunitXmlFilenameExtension>("junitXmlFilename") {
    outputDir.set(layout.buildDirectory.dir("reports/junit-enriched-demo"))
}
