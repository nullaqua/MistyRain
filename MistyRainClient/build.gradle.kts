import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems.jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    application
    kotlin("jvm") version "1.9.0"
    id ("org.openjfx.javafxplugin") version "0.0.10"
}

group = "me.nullaqua"
version = project.parent!!.version
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    maven("https://maven.aliyun.com/nexus/content/groups/public/")
}

dependencies {
    //com.vladsch.flexmark:flexmark-all:0.64.8
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")
    //com.formdev:flatlaf
    implementation("com.formdev:flatlaf:3.1.1")
    implementation("com.formdev:flatlaf-extras:3.1.1" )
    implementation("com.formdev:flatlaf-fonts-inter:+" )
    implementation("com.formdev:flatlaf-fonts-jetbrains-mono:+" )
    implementation("com.formdev:flatlaf-fonts-roboto:+" )
    implementation("com.formdev:flatlaf-fonts-roboto-mono:+" )
    implementation("com.formdev:flatlaf-intellij-themes:3.1.1" )
    //BluestarAPI
    implementation("me.nullaqua:BluestarAPI-kotlin:${project.properties["bluestarAPI"]}")
    //协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.0")
    //父模块
    implementation(project(":"))


    //javafx
    //implementation("org.openjfx:javafx-media:17.0.2")
    //implementation("org.openjfx:javafx-swing:11.0.2")
    //implementation("org.openjfx:javafx-web:11.0.2")
    //implementation("org.openjfx:javafx-base:11.0.2")
    //implementation("org.openjfx:javafx-graphics:11.0.2")
    //implementation("org.openjfx:javafx-controls:11.0.2")
}
javafx {
    version = "11"
    modules("javafx.media", "javafx.swing", "javafx.web", "javafx.base", "javafx.graphics", "javafx.controls")
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
            attributes["Main-Class"] = "me.nullaqua.mistyrain.Main"
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        this.from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}