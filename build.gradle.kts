plugins {
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

allprojects {
    group = "org.htilssu"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
        maven("https://oss.sonatype.org/content/groups/public/") {
            name = "sonatype"
        }
    }

    // Cấu hình Java toolchain đơn giản hơn cho tất cả các dự án
    plugins.withType<JavaPlugin> {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17)) // Sử dụng Java 17 thay vì 21
            }
        }
    }
}

dependencies { // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    // Include all submodules
    implementation(project(":core"))
    implementation(project(":v1_18"))
    implementation(project(":v1_19"))
    implementation(project(":v1_20"))
}

tasks {
    shadowJar {
        archiveBaseName.set("FlyFuel")

        // Include all submodules in the shadow jar
        dependencies {
            include(dependency("org.htilssu:core"))
            include(dependency("org.htilssu:v1_18"))
            include(dependency("org.htilssu:v1_19"))
            include(dependency("org.htilssu:v1_20"))
        }
    }

    runServer {
        minecraftVersion("1.18")
    }

    build {
        dependsOn(shadowJar)
    }
}

// Sử dụng Java 17 thay vì 21
val targetJavaVersion = 17
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.processResources {
    val props = mapOf(
        "version" to version
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

