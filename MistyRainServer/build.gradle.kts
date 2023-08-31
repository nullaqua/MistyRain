import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.9.0"
}

group = "me.nullaqua"
//版本号从父模块继承
version = project.parent!!.version
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    maven("https://maven.aliyun.com/nexus/content/groups/public/")
    mavenCentral()
}

dependencies {
    //BluestarAPI
    implementation("me.nullaqua:BluestarAPI-kotlin:${project.properties["bluestarAPI"]}")
    //协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.0")
    //父模块
    implementation(project(":"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
//打包依赖项
tasks {
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "me.nullaqua.mistyrain.MainKt"
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        this.from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}