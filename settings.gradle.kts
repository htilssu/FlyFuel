rootProject.name = "FlyFuel"

// Include core module and version-specific modules
include("core", "v1_18", "v1_19", "v1_20")

// Cấu hình toolchain repositories để tự động tải JDK
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}
