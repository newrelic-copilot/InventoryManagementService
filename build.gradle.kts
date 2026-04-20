import org.gradle.wrapper.Download

plugins {
    id("java")
    id("org.springframework.boot") version "2.5.10"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("de.undercouch.download") version "5.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register("downloadNewrelic") {
    doLast {
            val newrelicDir = file("newrelic")
            if (!newrelicDir.exists()) {
                newrelicDir.mkdirs()
            }
        ant.invokeMethod("get", mapOf(
            "src" to "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip",
            "dest" to file("newrelic/newrelic-java.zip")
        ))
    }
}

tasks.register<Copy>("unzipNewrelic") {
    from(zipTree(file("newrelic/newrelic-java.zip")))
    into(rootDir)
}

dependencies {
    implementation ("commons-fileupload:commons-fileupload:1.3.3")
    implementation ("org.apache.commons:commons-lang3:3.9")
    implementation ("org.apache.commons:commons-collections4:4.4")

    implementation ("org.springframework.boot:spring-boot-starter-web")

    implementation ("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation ("org.apache.logging.log4j:log4j-api:2.14.1")

    implementation ("com.google.code.gson:gson:2.8.9")

    implementation ("com.google.guava:guava:18.0")

    implementation ("com.fasterxml.jackson.core:jackson-databind:2.8.11")
    implementation ("com.fasterxml.jackson.core:jackson-core:2.8.11")
    implementation ("com.fasterxml.jackson.core:jackson-annotations:2.8.11")

    implementation ("commons-net:commons-net:3.6")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}
