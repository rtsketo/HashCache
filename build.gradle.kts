val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.0"
}

group = "eu.rtsketo"
version = "0.0.1"
application {
    mainClass.set("eu.rtsketo.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")

    implementation("io.ktor:ktor-serialization:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.erdtman:java-json-canonicalization:1.1")

    implementation("com.github.rtsketo:ktee:1.0.1")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}