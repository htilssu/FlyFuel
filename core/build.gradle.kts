plugins {
    kotlin("jvm")
}

dependencies {
    // Use a generic Paper API version for compilation
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    
    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

// No need to specify Java toolchain here as it's defined in the root project