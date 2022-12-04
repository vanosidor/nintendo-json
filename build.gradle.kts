import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization").version("1.7.10")
}

group = "me.ivan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}



dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("com.algolia:algoliasearch-client-kotlin:2.0.0")
}