plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    kotlin("plugin.serialization") version libs.versions.kotlin
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.lanterna)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kaml)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest(libs.versions.kotlin)
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "xyz.malefic.RunnerKt"
}
