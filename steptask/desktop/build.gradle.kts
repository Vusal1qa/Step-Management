plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "com.example.desktop.DesktopMainKt"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
            packageName = "StepTaskDesktop"
            packageVersion = "0.1.0"
        }
    }
}
