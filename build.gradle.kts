plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1" // For creating fat JARs
}

group = "com.eventstreams.connectors"
version = "1.0.0"


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    // Kafka Connect API
    implementation("org.apache.kafka:connect-api:3.6.1")

    // SLF4J for logging
    implementation("org.slf4j:slf4j-api:2.0.9")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("student-http-source-connector")
        archiveClassifier.set("all")
        archiveVersion.set(provider { version.toString() }) // ✅ Fix for error
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}