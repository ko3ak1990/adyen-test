plugins {
    base
    kotlin("jvm") version "2.0.21" apply false
    kotlin("android") version "2.0.21" apply false
    id("com.android.application") version "8.5.2" apply false
}

allprojects {
    group = "com.adyen"
    version = "0.1.0"
}
