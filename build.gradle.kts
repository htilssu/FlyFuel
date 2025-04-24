plugins {
    kotlin("jvm") version "1.9.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
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

// Đường dẫn tới thư mục plugins của Minecraft server
// Hãy thay đổi đường dẫn này tới thư mục plugins của server của bạn
val serverPluginsDir = "C:/Users/tolas/Downloads/server/plugins"

tasks {
    shadowJar {
        archiveBaseName.set("FlyFuel")

        dependencies {
            include(dependency("org.htilssu:core"))
            include(dependency("org.htilssu:v1_18"))
            include(dependency("org.htilssu:v1_19"))
            include(dependency("org.htilssu:v1_20"))
            // Include Kotlin runtime
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7"))
            include(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        }
        
        // Relocate Kotlin packages để tránh xung đột
        relocate("kotlin", "org.htilssu.flyFuel.kotlin")
    }

    build {
        dependsOn(shadowJar)
    }
    
    // Task mới để copy file JAR vào thư mục plugins của server
    register<Copy>("deployToServer") {
        dependsOn(build)
        
        // Kiểm tra và tạo thư mục đích nếu nó không tồn tại
        doFirst {
            val directory = file(serverPluginsDir)
            if (!directory.exists()) {
                directory.mkdirs()
                println("Created plugins directory: $serverPluginsDir")
            }
            
            // Kiểm tra xem file JAR nguồn có tồn tại không
            val jarFile = shadowJar.get().archiveFile.get().asFile
            if (!jarFile.exists()) {
                throw GradleException("Source JAR file does not exist: ${jarFile.absolutePath}")
            }
            println("Source JAR file exists: ${jarFile.absolutePath}")
        }
        
        from(shadowJar)
        into(serverPluginsDir)
        doLast {
            println("Plugin JAR has been copied to server plugins directory: $serverPluginsDir")
        }
    }

    // Ghi đè task shadowJar để nó tự động chạy deployToServer
    named("shadowJar") {
        finalizedBy("deployToServer")
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

