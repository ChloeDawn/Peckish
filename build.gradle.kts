plugins {
    id("net.minecraftforge.gradle") version "3.0.174"
}

group = "dev.sapphic"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

minecraft {
    mappings("stable", "39-1.12")
    runs.create("client") { taskName = "Client" }
    runs.create("server") { taskName = "Server" }
}

dependencies {
    minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2854")
    implementation("org.checkerframework:checker-qual:3.4.0")
}

tasks.withType<ProcessResources> {
    filesMatching(setOf("/mcmod.info", "/version.properties")) {
        expand("version" to version)
    }
}

tasks.withType<JavaCompile> {
    with(options) {
        isFork = true
        encoding = "UTF-8"
        compilerArgs.add("-Xlint:all")
    }
}
