plugins {
    kotlin("js") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.1"
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}

// Produces a static bundle (index.html + JS) under build/distributions
