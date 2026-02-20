plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    application
}

application {
    mainClass.set("com.gfu.coroutines.MainKt")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
