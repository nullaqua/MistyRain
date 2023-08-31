import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val bluestarAPI:String by project

plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "me.nullaqua"
version = "0.1.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("me.nullaqua:BluestarAPI-kotlin:${bluestarAPI}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}