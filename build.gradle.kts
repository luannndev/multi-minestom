plugins {
    id("java")

    id("com.gradleup.shadow") version "8.3.0"
}

group = "dev.luan.server"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(fileTree("libs") {
        include("*.jar")
    })

    implementation("net.kyori:adventure-text-minimessage:4.17.0")

    // log4j
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-beta2")

    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
    implementation("ch.qos.logback:logback-classic:1.5.7")

    implementation("net.minestom:minestom-snapshots:a521c4e7cd")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.shadowJar {
    archiveFileName.set("multistom-stable.jar")
    manifest {
        attributes["Main-Class"] = "dev.luan.server.ServerBootstrap"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}