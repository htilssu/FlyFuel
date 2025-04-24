plugins {
    kotlin("jvm")
}

dependencies {
    // Depend on the core module
    implementation(project(":core"))
    
    // Specific version dependency for 1.18
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    
    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

// Java toolchain is configured in the root project